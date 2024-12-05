package com.fastcampus.batchcampus.batch.generator;

import com.fastcampus.batchcampus.domain.ApiOrder;
import com.fastcampus.batchcampus.domain.ServicePolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

@Component // Spring의 컴포넌트로 등록
public class ApiOrderGenerateProcessor implements ItemProcessor<Boolean, ApiOrder> {

    // 고객 ID 목록: 0부터 19까지의 Long 값 생성
    private final List<Long> customerIds = LongStream.range(0, 20).boxed().toList();
    // 서비스 정책 목록: ServicePolicy enum의 모든 값을 리스트로 변환
    private final List<ServicePolicy> servicePolicies = Arrays.stream(ServicePolicy.values()).toList();
    // 랜덤 생성기: 스레드 안전한 랜덤 생성기
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    // 날짜 및 시간 형식 지정: "yyyyMMddHHmmss" 형식
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // ItemProcessor 인터페이스의 process 메서드 구현
    @Override
    public ApiOrder process(Boolean item) throws Exception {
        // 랜덤 고객 ID 선택
        final Long randomCustomerId = customerIds.get(random.nextInt(customerIds.size()));
        // 랜덤 서비스 정책 선택
        final ServicePolicy randomServicePolicy = servicePolicies.get(random.nextInt(servicePolicies.size()));
        // 랜덤 상태 결정: 20% 확률로 실패 상태 설정
        final ApiOrder.State randomState = random.nextInt(5) % 5 == 1 ?
                ApiOrder.State.FAIL : ApiOrder.State.SUCCESS;

        // ApiOrder 객체 생성하여 반환
        return new ApiOrder(
                UUID.randomUUID().toString(), // 랜덤 UUID 생성
                randomCustomerId, // 선택된 고객 ID
                randomServicePolicy.getUrl(), // 선택된 서비스 정책의 URL
                randomState, // 결정된 상태 (성공 또는 실패)
                LocalDateTime.now().format(dateTimeFormatter) // 현재 시간 포맷팅
        );
    }
}
