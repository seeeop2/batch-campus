package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.batch.ItemProcessor;
import com.fastcampus.batchcampus.customer.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// 고객의 로그인 날짜를 기준으로 휴면 전환 대상을 처리하는 ItemProcessor 구현
@Component
public class PreDormantBatchItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) {

        // 휴면 전환 기준 날짜 설정: 현재 날짜에서 365일을 빼고 7일 추가
        final LocalDate targetDate = LocalDate.now()
                .minusDays(365)
                .plusDays(7);

        // 고객의 로그인 날짜가 기준 날짜와 일치하는지 확인
        if (targetDate.isEqual(customer.getLoginAt().toLocalDate())){
            return customer; // 일치할 경우 해당 고객 반환
        } else {
            return null; // 일치하지 않으면 null 반환
        }
    }

}
