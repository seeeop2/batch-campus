package com.fastcampus.batchcampus.application;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.batch.Tasklet;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// 비즈니스 로직 담당

@Component
public class DormantBatchTasklet implements Tasklet {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public DormantBatchTasklet(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    @Override
    public void execute() {

        int pageNo = 0; // 페이지 번호 초기화

        while (true) {
            // 1. 유저를 조회한다.
            final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending());
            final Page<Customer> page = customerRepository.findAll(pageRequest);

            final Customer customer;

            // 페이지가 비어있다면, 배치 작업이 끝난 것으로 판단하고 중단함.
            if (page.isEmpty()) {
                break;
            // 페이지가 있다면, 페이지 번호를 올리고, 페이지 내 존재하는 내용을 Customer 객체에 담음.
            } else {
                pageNo++; // 다음 페이지로 이동
                customer = page.getContent().get(0); // 첫 번째 고객 정보 가져오기
            }

            // 2. 휴면계정 대상을 추출 및 변환한다.
            final boolean isDormantTarget = LocalDate.now()
                    .minusDays(365) // 현재 날짜에서 365일 전
                    .isAfter(customer.getLoginAt().toLocalDate()); // 마지막 로그인 날짜와 비교

            if (isDormantTarget) {
                customer.setStatus(Customer.Status.DORMANT); // 휴면 상태로 변경
            } else {
                // 휴면 대상이 아니면 다음 고객으로 넘어감
                continue;
            }

            // 3. 휴면계정으로 상태를 변경한다.
            customerRepository.save(customer); // 변경된 고객 정보 저장

            // 4. 메일을 보낸다.
            emailProvider.send(customer.getEmail(), "휴면 전환 안내 메일입니다.", "내용"); // 고객에게 이메일 전송
        }
    }
}
