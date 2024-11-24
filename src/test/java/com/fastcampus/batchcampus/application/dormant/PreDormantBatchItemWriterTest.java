package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.customer.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// PreDormantBatchItemWriter 클래스의 테스트를 위한 테스트 클래스
class PreDormantBatchItemWriterTest {

    private PreDormantBatchItemWriter preDormantBatchItemWriter; // 테스트할 ItemWriter 인스턴스

    // 이메일 전송 기능을 검증하는 테스트
    @Test
    @DisplayName("1주일 뒤에 휴면계정전환 예정자라고 이메일을 전송해야한다.")
    void test1(){
        // given: EmailProvider의 Mock 객체 생성 및 PreDormantBatchItemWriter 초기화
        final EmailProvider mockEmailProvider = mock(EmailProvider.class); // Mockito를 사용하여 Mock 객체 생성
        this.preDormantBatchItemWriter = new PreDormantBatchItemWriter(mockEmailProvider); // Mock 객체 주입
        final Customer customer = new Customer("minsoo", "minsoo@fastcampus.com"); // 테스트할 고객 객체 생성

        // when: 고객에게 이메일 전송 메서드 호출
        preDormantBatchItemWriter.write(customer);

        // then: 이메일 전송 메서드가 호출되었는지 검증
        verify(mockEmailProvider, atLeastOnce()).send(any(), any(), any()); // send 메서드가 최소 한 번 호출되어야 함
    }
}