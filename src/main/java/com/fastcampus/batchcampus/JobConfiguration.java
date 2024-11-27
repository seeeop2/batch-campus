package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobConfiguration {

    // Job 빈 생성: JobRepository와 Step을 사용하여 Job 객체 생성
    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("job", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .start(step) // Job의 시작 단계로 step 지정
                .build();
    }

    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager platformTransactionManager){

        // Tasklet 구현: 반복 실행 로직을 포함한 Tasklet 생성
        final Tasklet tasklet = new Tasklet() {
            private int count = 0; // 실행 카운터 초기화

            @Override
            public RepeatStatus execute(StepContribution a, ChunkContext b) throws Exception {
                count++; // 카운터 증가

                // 카운터가 15에 도달하면 Tasklet 종료
                if (count == 15){
                    log.info("Tasklet FINISHED"); // 종료 로그 출력
                    return RepeatStatus.FINISHED; // Step 수행 완료 상태 반환
                }

                log.info("Tasklet CONTINUABLE {}", count); // 카운터 값 로그 출력
                return RepeatStatus.CONTINUABLE; // Step 계속 실행 상태 반환
            }
        };

        return new StepBuilder("step", jobRepository) // StepBuilder를 사용하여 Step 이름과 JobRepository 설정
                .tasklet(tasklet, platformTransactionManager) // PlatformTransactionManager를 사용하여 트랜잭션 관리
                .build();
    }
}
