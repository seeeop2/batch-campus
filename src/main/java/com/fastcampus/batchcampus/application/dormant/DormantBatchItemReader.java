package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.batch.ItemReader;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class DormantBatchItemReader implements ItemReader<Customer> {

    private final CustomerRepository customerRepository;
    private int pageNo = 0; // 페이지 번호 초기화

    public DormantBatchItemReader(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer read() {
        final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending()); // 현재 페이지 번호와 페이지 크기(1)를 설정하여 PageRequest 생성

        final Page<Customer> page = customerRepository.findAll(pageRequest); // 고객 정보를 페이지 단위로 조회

        // 조회 결과가 비어있으면
        if (page.isEmpty()) {
            pageNo = 0; // 페이지 번호를 초기화
            return null;
        } else { // 페이지가 있다면, 페이지 번호를 올리고, 페이지 내 존재하는 내용을 Customer 객체에 담음.
            pageNo++; // 다음 페이지로 이동
            return page.getContent().get(0); // 첫 번째 고객 정보 가져오기
        }
    }

}
