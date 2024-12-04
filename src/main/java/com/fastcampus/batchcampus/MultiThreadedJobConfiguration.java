package com.fastcampus.batchcampus;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
//@Configuration
public class MultiThreadedJobConfiguration {

    // Job 빈 생성: JobRepository와 Step을 사용하여 multiThreadJob 객체 생성
    @Bean
    public Job job(JobRepository jobRepository,
                   Step step) {
        return new JobBuilder("multiThreadJob", jobRepository) // JobBuilder를 사용하여 Job 이름과 JobRepository 설정
                .start(step) // Job의 시작 단계로 step 지정
                .incrementer(new RunIdIncrementer()) // Job 실행 시마다 고유한 ID를 부여
                .build();
    }

    // Step 빈 생성: JobRepository와 PlatformTransactionManager를 사용하여 Step 객체 생성
    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager platformTransactionManager,
                     JpaPagingItemReader<User> jpapagingItemReader) {
        return new StepBuilder("step", jobRepository) // StepBuilder를 사용하여 Step 이름과 JobRepository 설정
                .<User, User>chunk(5, platformTransactionManager) // 청크 단위로 처리: 5개씩 읽고 트랜잭션 관리
                .reader(jpapagingItemReader) // ItemReader 설정: JPA를 사용하여 데이터 읽기
                .writer(result -> log.info(result.toString())) // ItemWriter 설정: 읽은 데이터를 로그에 출력
                .taskExecutor(new SimpleAsyncTaskExecutor()) // 비동기 처리Executor 설정: 멀티스레드 실행
                .build();
    }

    // JpaPagingItemReader 빈 생성: JPA를 사용하여 페이징 방식으로 데이터를 읽어오는 Reader 생성
    @Bean
    public JpaPagingItemReader<User> jpapagingItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<User>() // JpaPagingItemReaderBuilder를 사용하여 Reader 설정
                .name("jpapagingItemReader") // Reader 이름 설정
                .entityManagerFactory(entityManagerFactory) // JPA EntityManager 설정
                .pageSize(5) // 한 페이지에 읽어올 데이터 수 설정
                .saveState(false) // 상태 저장 여부 설정: false면 상태를 저장하지 않음.
                                           // 병렬 처리/상태 관리 복잡성/재시작 시 유연성 등의 이유로 멀티 쓰레드 환경에서는 FALSE를 권장함.
                .queryString("SELECT u FROM User u ORDER BY u.id") // 데이터 조회 쿼리 설정
                .build();
    }
}
