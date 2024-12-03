package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j // 로깅을 위한 어노테이션
@Configuration
public class ParallelStepJobConfig {

    // Job 구성: flow1과 flow2를 실행한 후 step4로 진행
    // flow1 (step1, step2)
    //                       --> step4
    //    flow2 (step3)
    @Bean
    public Job job(JobRepository jobRepository,
                   Step step4,
                   Flow splitFlow) {
        return new JobBuilder("job", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .start(splitFlow) // Job의 시작 단계로 splitFlow 지정
                .next(step4) // splitFlow가 완료된 후 step4로 진행
                .build()
                .build();
    }

    // Flow 구성: flow1과 flow2를 동시에 실행하는 splitFlow 생성
    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor()) // 비동기 처리Executor 설정: flow1과 flow2를 병렬로 실행
                .add(flow1, flow2) // flow1과 flow2 추가
                .build();
    }

    // flow1 구성: step1을 실행한 후 step2로 진행
    @Bean
    public Flow flow1(Step step1, Step step2) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(step1) // flow1의 시작 단계로 step1 지정
                .next(step2) // step1이 완료된 후 step2로 진행
                .build();
    }

    // flow2 구성: step3만 실행
    @Bean
    public Flow flow2(Step step3) {
        return new FlowBuilder<SimpleFlow>("flow2") // flow2 이름 수정
                .start(step3) // flow2의 시작 단계로 step3 지정
                .build();
    }

    // step1 구성: 1초 대기 후 로그 출력
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((a,b) -> {
                    Thread.sleep(1000); // 1초 대기
                    log.info("step1"); // step1 로그 출력
                    return RepeatStatus.FINISHED; // 작업 완료 상태 반환
                }, platformTransactionManager)
                .build();
    }

    // step2 구성: 2초 대기 후 로그 출력
    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet((a,b) -> {
                    Thread.sleep(2000); // 2초 대기
                    log.info("step2"); // step2 로그 출력
                    return RepeatStatus.FINISHED; // 작업 완료 상태 반환
                }, platformTransactionManager)
                .build();
    }

    // step3 구성: 2.5초 대기 후 로그 출력
    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet((a,b) -> {
                    Thread.sleep(2500); // 2.5초 대기
                    log.info("step3"); // step3 로그 출력
                    return RepeatStatus.FINISHED; // 작업 완료 상태 반환
                }, platformTransactionManager)
                .build();
    }

    // step4 구성: 1초 대기 후 로그 출력
    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet((a,b) -> {
                    Thread.sleep(1000); // 1초 대기
                    log.info("step4"); // step4 로그 출력
                    return RepeatStatus.FINISHED; // 작업 완료 상태 반환
                }, platformTransactionManager)
                .build();
    }

}
