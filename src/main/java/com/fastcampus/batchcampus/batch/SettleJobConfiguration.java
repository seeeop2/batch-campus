package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.batch.support.DateFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Configuration
public class SettleJobConfiguration {

    private final JobRepository jobRepository;

    // settleJob을 정의하는 메서드: preSettleDetailStep과 settleDetailStep을 포함
    @Bean
    public Job settleJob(Step preSettleDetailStep,
                         Step settleDetailStep,
                         Step settleGroupStep) {
        return new JobBuilder("settleJob", jobRepository) // JobBuilder를 사용하여 settleJob 생성
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"})) // Job 파라미터 검증기 설정
                .start(preSettleDetailStep) // 첫 번째 스텝으로 preSettleDetailStep 설정
                .next(settleDetailStep) // 다음 스텝으로 settleDetailStep 설정
                .next(isFridayDecider()) // 주간 정산 하는 날이면
                .on("COMPLETED").to(settleGroupStep) // 주간 정산 실행시켜줘
                .build()
                .build();
    }

    // 매주 금요일마다 주간 정산을 한다.
    public JobExecutionDecider isFridayDecider(){
        return (jobExecution, stepExecution) -> {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 날짜 형식 지정
            final String targetDate = stepExecution.getJobParameters().getString("targetDate"); // Job 파라미터에서 targetDate 가져오기
            final LocalDate date = LocalDate.parse(targetDate, formatter); // 문자열을 LocalDate로 변환

            // 주어진 날짜가 금요일인지 확인
            if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
                return new FlowExecutionStatus("NOOP"); // 금요일이 아니면 NOOP 상태 반환
            }
            return FlowExecutionStatus.COMPLETED; // 금요일이면 COMPLETED 상태 반환
        };
    }
}
