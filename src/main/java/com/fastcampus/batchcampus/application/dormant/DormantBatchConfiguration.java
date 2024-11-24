package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.batch.Job;
import com.fastcampus.batchcampus.batch.Step;
import com.fastcampus.batchcampus.batch.StepJobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Spring의 Configuration 클래스로, 배치 작업에 대한 설정을 정의

@Configuration
public class DormantBatchConfiguration {

    // 배치 작업을 정의하는 Job 빈 생성
    @Bean
    public Job dormantBatchJob(Step preDormantBatchStep,
                               Step dormantBatchStep,
                               DormantBatchJobExecutionListener listener){

        // Builder 패턴을 사용하여 Job 객체를 생성
        return new StepJobBuilder()
                .start(preDormantBatchStep)
                .next(dormantBatchStep)
                .build();
    }

    // 전처리 단계 Step 정의: 모든 고객을 읽고, 휴면 전환을 위한 전처리 수행
    @Bean
    public Step preDormantBatchStep(AllCustomerItemReader itemReader,
                                    PreDormantBatchItemProcessor itemProcessor,
                                    PreDormantBatchItemWriter itemWriter){
        return Step.builder()
                .itemReader(itemReader) // 고객 정보를 읽기 위한 ItemReader 설정
                .itemProcessor(itemProcessor) // 고객 정보를 처리하기 위한 ItemProcessor 설정
                .itemWriter(itemWriter) // 처리된 정보를 저장하기 위한 ItemWriter 설정
                .build(); // Step 객체 생성
    }

    // 본처리 단계 Step 정의: 휴면 계정 처리 작업 수행
    @Bean
    public Step dormantBatchStep(AllCustomerItemReader itemReader,
                                 DormantBatchItemProcessor itemProcessor,
                                 DormantBatchItemWriter itemWriter){
        return Step.builder()
                .itemReader(itemReader) // 고객 정보를 읽기 위한 ItemReader 설정
                .itemProcessor(itemProcessor) // 고객 정보를 처리하기 위한 ItemProcessor 설정
                .itemWriter(itemWriter) // 처리된 정보를 저장하기 위한 ItemWriter 설정
                .build(); // Step 객체 생성
    }
}
