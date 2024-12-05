package com.fastcampus.batchcampus.batch.generator;

import com.fastcampus.batchcampus.domain.ApiOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ApiOrderGenerateJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // Job 구성: apiOrderGenerateJob 이름으로 Job 생성
    @Bean
    public Job apiOrderGenerateJob(Step step) {
        return new JobBuilder("apiOrderGenerateJob", jobRepository)
                .start(step) // Job의 시작 단계로 step 지정
                .incrementer(new RunIdIncrementer()) // Job 실행 시 ID를 증가시켜 중복 실행 방지
                .validator( // Job 파라미터 유효성 검증기 설정
                        new DefaultJobParametersValidator(
                                new String[]{"targetDate", "totalCount"}, // 필수 파라미터
                                new String[0] // 선택적 파라미터 (없음)
                        )
                )
                .build();
    }

    // Step 구성: apiOrderGenerateStep 이름으로 Step 생성
    @Bean
    public Step step(ApiOrderGenerateReader apiOrderGenerateReader,
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
            @Value("#{jobParameters['targetDate']}") String targetDate
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
