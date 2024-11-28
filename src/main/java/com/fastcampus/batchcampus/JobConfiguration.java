package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
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
        return new JobBuilder("job-chunk", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .start(step) // Job의 시작 단계로 step 지정
                .build();
    }

    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager platformTransactionManager){
        // ItemReader 구현: 정수 값을 읽어오는 Reader 생성
        final ItemReader<Integer> itemReader = new ItemReader<>() {
            private int count = 0; // 읽은 항목 수를 카운트

            @Override
            public Integer read() {
                count++; // 카운터 증가

                log.info("Read : {}", count); // 현재 읽은 값 로그 출력

                // 카운터가 15에 도달하면 null 반환 (읽기 종료)
                if (count >= 15) {
                    throw new IllegalStateException("예외가 발생했어요.");
                }
                return count; // 현재 카운터 값을 반환
            }
        };

        // StepBuilder를 사용하여 Step 객체 생성
        return new StepBuilder("step", jobRepository) // StepBuilder를 사용하여 Step 이름과 JobRepository 설정
                .chunk(10, platformTransactionManager) // 청크 단위로 처리: 10개씩 읽고 트랜잭션 관리
                .reader(itemReader) // ItemReader 설정
                // .processor() // 데이터 처리기 설정 (현재는 주석 처리됨)
                .writer(read -> {}) // ItemWriter 설정: 현재는 빈 구현
                .faultTolerant() // 오류 발생 시에도 작업을 계속 진행할 수 있도록 설정
                // .skip(IllegalStateException.class) // 특정 예외에 대해 스킵 설정 (간단한 요구사항 경우 사용)
                // .skipLimit(3) // 스킵 한도 설정 (간단한 요구사항 경우 사용)
                .skipPolicy((t, skipCount) -> t instanceof IllegalStateException && skipCount < 5) // 스킵 정책 설정: IllegalStateException 발생 시 최대 5회까지 스킵 (복잡한 요구사항 경우 사용)
                .build();
    }
}
