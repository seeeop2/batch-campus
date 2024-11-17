package com.fastcampus.batchcampus.application;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.batch.ItemWriter;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class DormantBatchItemWriter implements ItemWriter<Customer> {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public DormantBatchItemWriter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    @Override
    public void write(Customer item) {
        customerRepository.save(item); // 변경된 고객 정보 저장
        emailProvider.send(item.getEmail(), "휴면 전환 안내 메일입니다.", "내용"); // 고객에게 이메일 전송
    }
}
