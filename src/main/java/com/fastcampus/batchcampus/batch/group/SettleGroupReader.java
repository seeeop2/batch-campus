package com.fastcampus.batchcampus.batch.group;

import com.fastcampus.batchcampus.domain.Customer;
import com.fastcampus.batchcampus.domain.repository.CustomerRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;

// SettleGroupReader 클래스: 고객 정보를 읽어오는 ItemReader 구현
@Component
public class SettleGroupReader implements ItemReader<Customer> {

    private final CustomerRepository customerRepository; // 고객 정보를 조회할 리포지토리
    private Iterator<Customer> customerIterator; // 고객 정보를 순회할 이터레이터
    private int pageNo = 0; // 현재 페이지 번호

    // 기본 생성자: Fake 구현체를 사용하여 고객 리포지토리 초기화
    public SettleGroupReader() {
        this.customerRepository = new CustomerRepository.Fake(); // 테스트용 가짜 리포지토리 사용
        customerIterator = Collections.emptyIterator(); // 초기 이터레이터는 빈 이터레이터로 설정
    }

    // 고객 정보를 읽어오는 메서드
    @Override
    public Customer read() {
        // 이터레이터에 다음 요소가 있으면 반환
        if (customerIterator.hasNext()) {
            return customerIterator.next();
        }

        // 다음 페이지의 고객 정보를 조회하여 이터레이터 초기화
        customerIterator = customerRepository.findAll(PageRequest.of(pageNo++, 10)).iterator();

        // 이터레이터에 더 이상 고객 정보가 없으면 null 반환
        if (!customerIterator.hasNext()) {
            return null;
        }

        return customerIterator.next(); // 다음 고객 정보를 반환
    }
}
