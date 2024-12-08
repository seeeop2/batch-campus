package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.batch.support.DateFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SettleJobConfiguration {

    private final JobRepository jobRepository;

    // settleJob을 정의하는 메서드: preSettleDetailStep과 settleDetailStep을 포함
    @Bean
    public Job settleJob(Step preSettleDetailStep,
                         Step settleDetailStep) {
        return new JobBuilder("settleJob", jobRepository) // JobBuilder를 사용하여 settleJob 생성
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"})) // Job 파라미터 검증기 설정
                .start(preSettleDetailStep) // 첫 번째 스텝으로 preSettleDetailStep 설정
                .next(settleDetailStep) // 다음 스텝으로 settleDetailStep 설정
                .build();
    }
}
