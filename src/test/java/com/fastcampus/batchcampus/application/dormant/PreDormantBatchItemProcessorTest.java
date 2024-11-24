package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.customer.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

// PreDormantBatchItemProcessor 클래스의 테스트를 위한 테스트 클래스
class PreDormantBatchItemProcessorTest {

    private PreDormantBatchItemProcessor preDormantBatchItemProcessor;

    // 각 테스트 실행 전에 PreDormantBatchItemProcessor 초기화
    @BeforeEach
    void setUp() {
        preDormantBatchItemProcessor = new PreDormantBatchItemProcessor();
    }

    // 로그인 날짜가 기준 날짜와 일치할 때 고객을 반환하는지 테스트
    @Test
    @DisplayName("로그인 날짜가 오늘로부터 358일 전이면 customer를 반환해야한다.")
    void test1(){

        // given: 고객 객체 생성 및 설정
        final Customer customer = new Customer("minsoo", "minsoo@fastcampus.com");

        // 마지막 로그인 날짜가 1년 전이 되기 1주일 전으로 설정
        customer.setLoginAt(LocalDateTime.now().minusDays(365).plusDays(7));

        // when: 고객 객체를 처리
        final Customer result = preDormantBatchItemProcessor.process(customer);

        // then: 결과 검증
        Assertions.assertThat(result).isEqualTo(customer); // 반환된 고객이 입력 고객과 일치해야 함
        Assertions.assertThat(result).isNotNull(); // 결과는 null이 아니어야 함
    }

    // 로그인 날짜가 기준 날짜와 일치하지 않을 때 null을 반환하는지 테스트
    @Test
    @DisplayName("로그인 날짜가 오늘로부터 358일 전이 아니면 null을 반환해야한다.")
    void test2(){

        // given: 고객 객체 생성
        final Customer customer = new Customer("minsoo", "minsoo@fastcampus.com");

        // when: 고객 객체를 처리
        final Customer result = preDormantBatchItemProcessor.process(customer);

        // then: 결과 검증
        Assertions.assertThat(result).isNull(); // 결과는 null이어야 함
    }

}