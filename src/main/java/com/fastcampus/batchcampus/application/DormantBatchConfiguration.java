package com.fastcampus.batchcampus.application;

import com.fastcampus.batchcampus.batch.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Spring의 Configuration 클래스로, 배치 작업에 대한 설정을 정의

@Configuration
public class DormantBatchConfiguration {

    @Bean
    public Job dormantBatchJob(DormantBatchTasklet dormantBatchTasklet, DormantBatchJobExecutionListener dormantBatchJobExecutionListener){
        return new Job(dormantBatchTasklet, dormantBatchJobExecutionListener);
    }
}
