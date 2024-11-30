package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
//@Configuration
public class JobConfiguration {

    // Job 빈 생성: JobRepository와 Step을 사용하여 Job 객체 생성
    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("job-chunk", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .start(step) // Job의 시작 단계로 step 지정
                .build();
    }

    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
    @JobScope // Job 실행 시점에 따라 Step이 생성됨을 나타내는 어노테이션
    @Bean
    public Step step(JobRepository jobrepository,
                     PlatformTransactionManager platformTransactionManager,
                     @Value("#{jobParameters['name']}") String name){

        log.info("name: {}", name); // JobParameters에서 'name' 값을 로그로 출력
        return new StepBuilder("step", jobrepository)
                .tasklet((a,b) -> RepeatStatus.FINISHED, platformTransactionManager)
                .build();
    }

//    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
//    @Bean
//    public Step step(JobRepository jobRepository,
//                     PlatformTransactionManager platformTransactionManager){
//        // ItemReader 구현: 정수 값을 읽어오는 Reader 생성
//        final ItemReader<Integer> itemReader = new ItemReader<>() {
//            private int count = 0; // 읽은 항목 수를 카운트
//
//            @Override
//            public Integer read() {
//                count++; // 카운터 증가
//
//                log.info("Read : {}", count); // 현재 읽은 값 로그 출력
//
//                // 카운터가 20에 도달하면 null 반환 (읽기 종료)
//                if (count == 20) {
//                    return null; // 더 이상 읽을 항목이 없음을 나타냄
//                }
//
////                // 카운터가 15에 도달하면 IllegalStateException 예외 발생
////                if (count >= 15) {
////                    throw new IllegalStateException("예외가 발생했어요."); // 예외 발생
////                }
//
//                return count; // 현재 카운터 값을 반환
//            }
//        };
//
//        // ItemProcessor 구현: 읽은 값을 처리하는 Processor 생성
//        ItemProcessor<Integer, Integer> itemProcessor = new ItemProcessor<>() {
//            @Override
//            public Integer process(Integer item) throws Exception {
//                // 처리 중 item이 15일 경우 IllegalStateException 예외 발생
//                if (item == 15) {
//                    throw new IllegalStateException(); // 예외 발생
//                }
//                return item; // 처리된 항목 반환
//            }
//        };
//
//        // StepBuilder를 사용하여 Step 객체 생성
//        return new StepBuilder("step", jobRepository) // StepBuilder를 사용하여 Step 이름과 JobRepository 설정
//                .<Integer, Integer>chunk(10, platformTransactionManager) // 청크 단위로 처리: 10개씩 읽고 트랜잭션 관리
//                .reader(itemReader) // ItemReader 설정
//                .processor(itemProcessor) // 데이터 처리기 설정
//                .writer(read -> {}) // ItemWriter 설정: 현재는 빈 구현
//                .faultTolerant() // 오류 발생 시에도 작업을 계속 진행할 수 있도록 설정
//                .retry(IllegalStateException.class) // IllegalStateException에 대해 재시도 설정
//                .retryLimit(5) // 최대 5회 재시도 설정
//                .build();
//    }
}
