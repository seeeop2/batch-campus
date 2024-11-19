package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.batch.ItemProcessor;
import com.fastcampus.batchcampus.customer.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DormantBatchItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) {
        // 현재 날짜에서 365일 전 날짜를 계산하여 마지막 로그인 날짜와 비교
        final boolean isDormantTarget = LocalDate.now()
            .minusDays(365) // 현재 날짜에서 365일 전
            .isAfter(item.getLoginAt().toLocalDate()); // 마지막 로그인 날짜와 비교

        // 고객이 휴면 대상인지 확인
        if (isDormantTarget) {
            item.setStatus(Customer.Status.DORMANT); // 휴면 상태로 변경
            return item; // 변경된 고객 객체 반환
        } else {
            return null; // 휴면 대상이 아닐 경우 null 반환
        }
    }

}
