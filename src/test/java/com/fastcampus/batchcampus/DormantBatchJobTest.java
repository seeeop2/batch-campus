package com.fastcampus.batchcampus;

import com.fastcampus.batchcampus.batch.BatchStatus;
import com.fastcampus.batchcampus.batch.JobExecution;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DormantBatchJobTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DormantBatchJob dormantBatchJob;

    // 각 테스트 전에 데이터베이스 초기화
    @BeforeEach
    public void setup(){
        customerRepository.deleteAll();
    }

    @DisplayName("로그인 시간이 일 년을 경과한 고객이 세 명이고, 일 년 이내에 로그인한 고객이 다섯 명이면 3명의 고객이 휴면전환대상이다.")
    @Test
    void test1(){

        // given: 주어진 데이터를 사전에 정의하는 것

        // 365일 넘었기에 휴면계정 대상
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);

        // 365일 넘지 않았기에 휴면계정 대상 아님
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);

        // when: 테스트가 일어났을 때
        final JobExecution result = dormantBatchJob.execute();

        // then: 결과
        // 휴면 상태인 고객 수 계산
        final long dormantCount = customerRepository.findAll().stream().filter(it -> it.getStatus() == Customer.Status.DORMANT).count();

        // 예상 결과와 비교
        Assertions.assertThat(dormantCount).isEqualTo(3);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @DisplayName("고객이 열 명이 있지만, 모두 다 휴면전환대상이면(1년 경과한사람) 휴면전환 대상은 10명이다.")
    @Test
    void test2(){
        // given: 고객 데이터 준비
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);

        // when: 배치 작업 실행
        final JobExecution result = dormantBatchJob.execute();

        // then: 결과
        // 휴면 상태인 고객 수 계산
        final long dormantCount = customerRepository.findAll().stream().filter(it -> it.getStatus() == Customer.Status.DORMANT).count();

        // 예상 결과와 비교
        Assertions.assertThat(dormantCount).isEqualTo(10);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @DisplayName("고객이 없는 경우에도 배치는 정상 동작해야 한다.")
    @Test
    void test3(){

        // when: 고객 데이터가 없는 경우 배치 작업 실행
        final JobExecution result = dormantBatchJob.execute();

        // then: 결과 확인
        // 휴면 상태인 고객 수 계산
        final long dormantCount = customerRepository.findAll().stream().filter(it -> it.getStatus() == Customer.Status.DORMANT).count();

        // 예상 결과와 비교
        Assertions.assertThat(dormantCount).isEqualTo(0);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("배치가 실패하면 BatchStatus는 FAILED를 반환해야한다.")
    void test4(){

        // given: 고객 리포지토리 주입 없이 배치 작업 인스턴스 생성
        DormantBatchJob dormantBatchJob = new DormantBatchJob(null);

        // when: 배치 작업 실행
        JobExecution result = dormantBatchJob.execute();

        // then: 결과 확인
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.FAILED);
    }

    // 고객을 저장하는 헬퍼 메서드
    private void saveCustomer(long loginMinusDays) {
        // 고유 ID 생성
        final String uuid = UUID.randomUUID().toString();
        // 고객 객체 생성
        final Customer test = new Customer(uuid, uuid + "@fastcampus.com");
        // 로그인 날짜 설정
        test.setLoginAt(LocalDateTime.now().minusDays(loginMinusDays));
        // 고객 정보 저장
        customerRepository.save(test);
    }

}
