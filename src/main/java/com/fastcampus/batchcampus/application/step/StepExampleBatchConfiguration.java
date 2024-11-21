package com.fastcampus.batchcampus.application.step;

import com.fastcampus.batchcampus.batch.Job;
import com.fastcampus.batchcampus.batch.Step;
import com.fastcampus.batchcampus.batch.StepJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

// 배치 작업의 단계 예제를 구성하는 설정 클래스
@Configuration
public class StepExampleBatchConfiguration {

    // StepJob 빈을 생성하는 메서드
    @Bean
    public Job stepExampleBatchJob(Step step1, Step step2, Step step3) {
        List<Step> steps = Arrays.asList(step1, step2, step3); // Step 목록을 배열로 생성
        return new StepJob(steps, null); // StepJob 객체 생성 후 반환
    }

    // step1 실행 로직을 가지는 Step 빈 생성
    @Bean
    public Step step1() {
        return new Step(() -> System.out.println("step1"));
    }

    // step2 실행 로직을 가지는 Step 빈 생성
    @Bean
    public Step step2() {
        return new Step(() -> System.out.println("step2"));
    }

    // step3 실행 로직을 가지는 Step 빈 생성
    @Bean
    public Step step3() {
        return new Step(() -> System.out.println("step3"));
    }
}
