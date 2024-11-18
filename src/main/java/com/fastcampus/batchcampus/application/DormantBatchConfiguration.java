package com.fastcampus.batchcampus.application;

import com.fastcampus.batchcampus.batch.Job;
import com.fastcampus.batchcampus.batch.SimpleTasklet;
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

        // SimpleTasklet을 생성하여 ItemReader, ItemProcessor, ItemWriter를 주입
        final SimpleTasklet tasklet = new SimpleTasklet(dormantBatchItemReader, dormantBatchItemProcessor, dormantBatchItemWriter);

        return new Job(tasklet, dormantBatchJobExecutionListener); // 생성된 Tasklet과 JobExecutionListener를 사용하여 Job 객체 반환
    }
}
