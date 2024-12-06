package com.fastcampus.batchcampus.batch.generator;

import com.fastcampus.batchcampus.domain.ApiOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class ApiOrderGeneratePartitionJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // Job 구성: apiOrderGenerateJob 이름으로 Job 생성
    @Bean
    public Job apiOrderGenerateJob(Step managerStep) {
        return new JobBuilder("apiOrderGenerateJob", jobRepository)
                .start(managerStep) // Job의 시작 단계로 step 지정
                .incrementer(new RunIdIncrementer()) // Job 실행 시 ID를 증가시켜 중복 실행 방지
                .validator( // Job 파라미터 유효성 검증기 설정
                    new DefaultJobParametersValidator(
                        new String[]{"targetDate", "totalCount"}, // 필수 파라미터
                        new String[0] // 선택적 파라미터 (없음)
                    )
                )
                .build();
    }

    @Bean
    @JobScope // 설정하는 이유: Job Parameters 주입 받기 위해서
    public Step managerStep(PartitionHandler partitionHandler,
                            @Value("#{jobParameters['targetDate']}") String targetDate,
                            Step apiOrderGenerateStep){
        return new StepBuilder("managerStep", jobRepository) // StepBuilder를 사용하여 "managerStep"이라는 이름의 Step 생성
                .partitioner("delegateStep", getPartitioner(targetDate)) // 지정된 targetDate를 사용하여 파티셔닝 설정
                .step(apiOrderGenerateStep) // 실제 작업을 수행할 apiOrderGenerateStep을 지정
                .partitionHandler(partitionHandler) // 파티션 처리를 위한 PartitionHandler 설정
                .build();
    }

    // 매니저 스텝이 워커 스텝을 어떻게 다룰지를 정의
    @Bean
    public PartitionHandler partitionHandler(Step apiOrderGenerateStep) {
        final TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler(); // TaskExecutorPartitionHandler 인스턴스 생성
        taskExecutorPartitionHandler.setStep(apiOrderGenerateStep); // 워커 스텝으로 사용할 apiOrderGenerateStep 설정
        taskExecutorPartitionHandler.setGridSize(7); // 파티션의 크기를 7로 설정 (7개의 파티션 생성)
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기 작업을 위한 TaskExecutor 설정
        return taskExecutorPartitionHandler; // 설정된 PartitionHandler 반환
    }

    // 워커 스텝을 위해서 StepExecution 을 생성하는 인터페이스
    Partitioner getPartitioner(String targetDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 날짜 포맷터 정의
        final LocalDate date = LocalDate.parse(targetDate, formatter); // 주어진 targetDate를 LocalDate로 파싱

        return x -> {
            final Map<String, ExecutionContext> result = new HashMap<>(); // 파티션 결과를 저장할 맵 생성

            // 0부터 6까지의 정수를 사용하여 7개의 파티션 생성
            IntStream.range(0, 7)
                .forEach(it -> {
                    final ExecutionContext value = new ExecutionContext(); // 각 파티션에 대한 ExecutionContext 생성
                    value.putString("targetDate", date.minusDays(it).format(formatter)); // 이전 날짜를 targetDate에 설정
                    result.put("partition" + it, value); // 파티션 이름과 ExecutionContext를 맵에 저장
                });
            return result; // 생성된 파티션 결과 반환
        };
    }

    // Step 구성: apiOrderGenerateStep 이름으로 Step 생성
    @Bean
    public Step apiOrderGenerateStep(ApiOrderGenerateReader apiOrderGenerateReader,
                                     ApiOrderGenerateProcessor apiOrderGenerateProcessor) {
        return new StepBuilder("apiOrderGenerateStep", jobRepository)
                .<Boolean, ApiOrder>chunk(5000, platformTransactionManager) // 청크 크기 및 트랜잭션 관리 설정
                .reader(apiOrderGenerateReader) // 데이터 읽기 위한 리더 설정
                .processor(apiOrderGenerateProcessor) // 데이터 처리기 설정
                .writer(apiOrderGenerateWriter(null)) // 데이터 쓰기 위한 writer 설정
                .build();
    }

    // StepScope 어노테이션으로 지정된 apiOrderGenerateWriter 빈: 파라미터로 targetDate 사용
    @StepScope
    @Bean
    public FlatFileItemWriter<ApiOrder> apiOrderGenerateWriter(
        @Value("#{stepExecutionContext['targetDate']}") String targetDate
    ) {
        final String fileName = targetDate + "_api_orders.csv"; // 파일 이름 생성
        return new FlatFileItemWriterBuilder<ApiOrder>()
                .name("apiOrderGenerateWriter") // writer 이름 설정
                .resource(new PathResource("src/main/resources/datas/" + fileName)) // 출력될 파일 경로 설정
                .delimited() // 구분자 설정
                .names("id", "customerId", "url", "state", "createdAt") // 필드 이름 설정
                .headerCallback(writer -> writer.write("id,customerId,url,state,createdAt")) // 파일 헤더 설정
                .build();
    }
}
