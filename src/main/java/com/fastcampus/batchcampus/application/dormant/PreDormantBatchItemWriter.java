package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.batch.ItemWriter;
import com.fastcampus.batchcampus.customer.Customer;
import org.springframework.stereotype.Component;

// 고객에게 휴면 전환 안내 이메일을 보내는 ItemWriter 구현
@Component
public class PreDormantBatchItemWriter implements ItemWriter<Customer> {

    private final EmailProvider emailProvider; // 이메일 전송을 위한 EmailProvider 인스턴스

    // 기본 생성자: Fake EmailProvider를 사용하여 초기화
    public PreDormantBatchItemWriter() {
        this.emailProvider = new EmailProvider.Fake();
    }

    // EmailProvider를 주입받는 생성자
    public PreDormantBatchItemWriter(EmailProvider emailProvider) {
        this.emailProvider = emailProvider; // 주입받은 EmailProvider로 초기화
    }

    // 고객에게 이메일을 보내는 메서드
    @Override
    public void write(Customer customer) {
        // 고객 이메일로 휴면 전환 안내 메시지 전송
        emailProvider.send(customer.getEmail(),
                "곧 휴면계정으로 전환이 됩니다.",
                "휴면계정으로 사용되기를 원치 않으신다면 1주일 내에 로그인을 해주세요."
        );
    }
}
