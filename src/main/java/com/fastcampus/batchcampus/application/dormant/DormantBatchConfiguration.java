package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.batch.Job;
import com.fastcampus.batchcampus.batch.TaskletJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Spring의 Configuration 클래스로, 배치 작업에 대한 설정을 정의

@Configuration
public class DormantBatchConfiguration {

    // 배치 작업을 정의하는 Job 빈 생성
    @Bean
    public Job dormantBatchJob(DormantBatchItemReader dormantBatchItemReader,
                               DormantBatchItemProcessor dormantBatchItemProcessor,
                               DormantBatchItemWriter dormantBatchItemWriter,
                               DormantBatchJobExecutionListener dormantBatchJobExecutionListener){

        // Builder 패턴을 사용하여 Job 객체를 생성
        return TaskletJob.builder()
                .itemReader(dormantBatchItemReader)
                .itemProcessor(dormantBatchItemProcessor)
                .itemWriter(dormantBatchItemWriter)
                .jobExecutionListener(dormantBatchJobExecutionListener)
                .build();
    }
}
