package com.fastcampus.batchcampus.example;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Configuration
public class PartitionJobConfiguration {

    // Job 구성: partitionJob 이름으로 Job 생성
    @Bean
    public Job job(JobRepository jobRepository, Step managerStep) {
        return new JobBuilder("partitionJob", jobRepository)
                .start(managerStep) // Job의 시작 단계로 managerStep 지정
                .incrementer(new RunIdIncrementer()) // Job 실행 시 ID를 증가시켜 중복 실행 방지
                .build(); // Job 객체 생성 및 반환
    }

    // managerStep 구성: 파티셔닝을 위한 단계 설정
    @Bean
    public Step managerStep(JobRepository jobRepository,
                            Step step,
                            PartitionHandler partitionHandler,
                            DataSource dataSource) {
        return new StepBuilder("managerStep", jobRepository)
                .partitioner("delegateStep", new ColumnRangePartitioner(dataSource)) // delegateStep에 대한 파티셔너 설정
                .step(step) // 실제 처리할 Step 설정
                .partitionHandler(partitionHandler) // 파티션 처리 방법 설정
                .build();
    }

    // PartitionHandler 구성: TaskExecutorPartitionHandler 사용
    @Bean
    public PartitionHandler partitionHandler(Step step) {
        final TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(step); // 사용할 Step 설정
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기 실행을 위한 TaskExecutor 설정
        taskExecutorPartitionHandler.setGridSize(5); // 파티션의 크기 설정
        return taskExecutorPartitionHandler; // PartitionHandler 반환
    }

    // 실제 처리할 Step 구성
    @Bean
    public Step step(JobRepository jobRepository,
                     JpaPagingItemReader<User> jpaPagingItemReader,
                     PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step", jobRepository)
                .<User, User>chunk(4, platformTransactionManager) // 청크 크기 설정
                .reader(jpaPagingItemReader) // 데이터 읽기 위한 리더 설정
                .writer(result -> log.info(result.toString())) // 읽은 데이터 로그 출력
                .build();
    }

    // StepScope 어노테이션으로 지정된 itemReader 빈: 파라미터로 minValue와 maxValue를 사용
    @StepScope
    @Bean
    public JpaPagingItemReader<User> itemReader(@Value("#{stepExecutionContext[minValue]}") Long minValue,
                                                @Value("#{stepExecutionContext[maxValue]}") Long maxValue,
                                                EntityManagerFactory entityManagerFactory){
        log.info("minValue: {}, maxValue: {}", minValue, maxValue); // minValue와 maxValue 로그 출력
        final Map<String, Object> params = new HashMap<>();
        params.put("minValue", minValue); // 파라미터 설정
        params.put("maxValue", maxValue);

        return new JpaPagingItemReaderBuilder<User>()
                .name("itemReader") // 리더 이름 설정
                .entityManagerFactory(entityManagerFactory) // EntityManagerFactory 설정
                .pageSize(5) // 페이지 크기 설정
                .queryString("""
                    SELECT u FROM User u
                    WHERE u.id BETWEEN :minValue AND :maxValue
                    """) // JPQL 쿼리 설정
                .parameterValues(params) // 쿼리 파라미터 설정
                .build();
    }
}
