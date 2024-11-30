package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class FlowConfiguration {

    // Spring의 Configuration 클래스로, 배치 작업에 대한 설정을 정의
    @Bean
    public Job flowJob(JobRepository jobRepository,
                       Step step1,
                       Step step2,
                       Step step3){

        // CASE 1: step1이 성공적으로 완료되면 step2로, 실패하면 step3으로 이동
        // - .on("*"): step1의 결과가 성공이든 실패든(step1이 완료된 상태) step2로 이동.
        // - .from(step1): step1에서의 결과를 기반으로 조건부 흐름을 정의.
        // - .on("FAILED"): step1이 실패한 경우에만 step3으로 이동.
        // - .end(): 흐름의 끝을 나타냄.

//        return new JobBuilder("flowJob", jobRepository)
//                .start(step1)
//                    .on("*").to(step2)
//                .from(step1)
//                    .on("FAILED").to(step3)
//                .end()
//                .build();

        // CASE 2: step1이 성공하면 step2로 이동, 실패하면 종료
        // - .on("*"): step1의 결과가 성공일 경우에 step2로 이동.
        // - .from(step1): step1에서의 결과를 기반으로 조건부 흐름을 정의.
        // - .on("FAILED").end(): step1이 실패한 경우, 더 이상 진행하지 않고 Job을 종료.

//        return new JobBuilder("flowJob", jobRepository)
//                .start(step1)
//                .on("*").to(step2)
//                .from(step1)
//                .on("FAILED").end()
//                .end()
//                .build();

        // CASE 3: step1이 성공하면 step2로 이동, 실패하면 작업 실패 처리
        // - .on("*"): step1의 결과가 성공일 경우 step2로 이동.
        // - .from(step1): step1에서의 결과를 기반으로 조건부 흐름을 정의.
        // - .on("FAILED").fail(): step1이 실패한 경우, Job을 실패로 처리하고 종료.

//        return new JobBuilder("flowJob", jobRepository)
//                .start(step1)
//                .on("*").to(step2)
//                .from(step1)
//                .on("FAILED").fail()
//                .end()
//                .build();

        // CASE 4: step1이 성공하면 step2를 중지하고 재시작
        // - .on("COMPLETED"): step1이 성공적으로 완료되었을 때의 조건을 나타냄.
        // - .stopAndRestart(step2): step1이 성공적으로 완료된 후, step2를 중지하고 재시작
        // - .end(): Job 흐름의 끝을 나타냄.

        return new JobBuilder("¬flowJob", jobRepository)
                .start(step1)
                .on("COMPLETED").stopAndRestart(step2) // step1이 완료된 후 step2 재시작
                .end()
                .build();
    }

    // Step1 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 step1 객체 생성
    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step1", jobRepository)
                .tasklet((a,b) -> {
                    log.info("step1 실행"); // step1 실행 시 로그 출력
//                    if (1 == 1) throw new IllegalStateException("step1 실패했어요.");
                    return null; // Tasklet 종료
                }, platformTransactionManager)
                .build();
    }

    // Step2 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 step2 객체 생성
    @Bean
    public Step step2(JobRepository jobRepository,
                      PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step2", jobRepository)
                .tasklet((a,b) -> {
                    log.info("step2 실행"); // step2 실행 시 로그 출력
                    return null; // Tasklet 종료
                }, platformTransactionManager)
                .build();
    }

    // Step3 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 step3 객체 생성
    @Bean
    public Step step3(JobRepository jobRepository,
                      PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step3", jobRepository)
                .tasklet((a,b) -> {
                    log.info("step3 실행"); // step3 실행 시 로그 출력
                    return null; // Tasklet 종료
                }, platformTransactionManager)
                .build();
    }
}
