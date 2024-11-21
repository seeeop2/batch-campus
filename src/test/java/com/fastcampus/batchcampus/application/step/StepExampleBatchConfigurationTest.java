package com.fastcampus.batchcampus.application.step;

import com.fastcampus.batchcampus.batch.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StepExampleBatchConfigurationTest {

    @Autowired
    private Job stepExampleBatchJob; // StepExampleBatchConfiguration에서 생성된 Job 빈 주입

    // 배치 작업 실행 테스트 메서드
    @Test
    void test(){
        stepExampleBatchJob.execute(); // 주입된 Job의 execute() 메서드 호출하여 배치 작업 실행
    }

}