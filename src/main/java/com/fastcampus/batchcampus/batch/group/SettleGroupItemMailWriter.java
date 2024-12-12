package com.fastcampus.batchcampus.batch.group;

import com.fastcampus.batchcampus.batch.support.EmailProvider;
import com.fastcampus.batchcampus.domain.Customer;
import com.fastcampus.batchcampus.domain.ServicePolicy;
import com.fastcampus.batchcampus.domain.SettleGroup;
import com.fastcampus.batchcampus.domain.repository.CustomerRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

// SettleGroupItemMailWriter 클래스: SettleGroup 리스트를 이메일로 발송하는 ItemWriter 구현

@Component
public class SettleGroupItemMailWriter implements ItemWriter<List<SettleGroup>> {
    private final CustomerRepository customerRepository; // 고객 정보를 조회할 리포지토리
    private final EmailProvider emailProvider; // 이메일 발송 기능을 제공하는 인터페이스

    // 기본 생성자: Fake 구현체를 사용하여 고객 리포지토리와 이메일 프로바이더 초기화
    public SettleGroupItemMailWriter() {
        this.customerRepository = new CustomerRepository.Fake(); // 테스트용 가짜 고객 리포지토리 사용
        this.emailProvider = new EmailProvider.Fake(); // 테스트용 가짜 이메일 프로바이더 사용
    }

    // 유료 API 총 사용 횟수, 총 요금
    // 세부사항에 대해서 (URL, 몇 건, 얼마)

    // Chunk를 받아 SettleGroup 리스트를 처리하여 이메일로 발송하는 메서드
    @Override
    public void write(Chunk<? extends List<SettleGroup>> chunk) throws Exception {
        // 각 SettleGroup 리스트를 반복 처리
        for (List<SettleGroup> settleGroups : chunk) {
            // 리스트가 비어있으면 다음으로 넘어감
            if (settleGroups.isEmpty()) {
                continue;
            }

            // 첫 번째 SettleGroup을 가져와 고객 ID 추출
            final SettleGroup settleGroup = settleGroups.get(0);
            final Long customerId = settleGroup.getCustomerId();

            final Customer customer = customerRepository.findById(customerId); // 고객 정보를 조회
            // 총 사용 횟수와 총 요금을 계산
            final Long totalCount = settleGroups.stream().map(SettleGroup::getTotalCount).reduce(0L, Long::sum);
            final Long totalFee = settleGroups.stream().map(SettleGroup::getTotalFee).reduce(0L, Long::sum);

            // 서비스별 세부 내역을 문자열 리스트로 생성
            final List<String> detailByService = settleGroups.stream()
                    .map(it ->
                            "\n\"%s\" - 총 사용수 : %s, 총 비용 : %s".formatted(
                                    ServicePolicy.findById(it.getServiceId()).getUrl(), // 서비스 URL 조회
                                    it.getTotalCount(), // 총 사용 수
                                    it.getTotalFee() // 총 비용
                            )
                    ).toList();

            // 이메일 본문 생성
            final String body = """
                    안녕하세요. %s 고객님. 사용하신 유료 API 과금 안내 드립니다.
                    총 %s건을 사용하셨으며, %s원의 비용이 발생했습니다.
                    세부 내역은 다음과 같습니다. 감사합니다.
                    %s
                    """.formatted(
                            customer.getName(),
                            totalCount,
                            totalFee,
                            detailByService
            );
            emailProvider.send(customer.getEmail(), "유료 API 과금 안내", body); // 이메일 전송
        }
    }
}
