package com.fastcampus.batchcampus.batch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// 여러 Step을 포함하는 배치 작업을 정의하는 StepJob 클래스
public class StepJob implements Job {

    private final List<Step> steps; // 실행할 Step 목록
    private final JobExecutionListener jobExecutionListener; // 작업 실행 전후 처리를 위한 리스너

    // 생성자: Step 목록과 JobExecutionListener를 주입받음
    public StepJob(List<Step> steps, JobExecutionListener jobExecutionListener) {
        this.steps = steps; // 전달받은 Step 목록 저장

        // JobExecutionListener가 null일 경우 기본 리스너 생성
        this.jobExecutionListener = Objects.requireNonNullElseGet(jobExecutionListener, () -> new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
            }
        });
    }

    // Job 인터페이스의 execute 메서드 구현
    @Override
    public JobExecution execute() {

        final JobExecution jobExecution = new JobExecution();
        jobExecution.setStatus(BatchStatus.STARTING); // 배치 작업 시작 상태 설정
        jobExecution.setStartTime(LocalDateTime.now()); // 시작 시간 기록

        jobExecutionListener.beforeJob(jobExecution); // 전처리

        try {
            steps.forEach(Step::execute);

            jobExecution.setStatus(BatchStatus.COMPLETED); // 배치 작업 완료 상태 설정
            jobExecutionListener.afterJob(jobExecution); // 후처리

        } catch (Exception e){
            jobExecution.setStatus(BatchStatus.FAILED); // 예외 발생 시 실패 상태 설정
        }

        jobExecution.setEndTime(LocalDateTime.now()); // 종료 시간 기록

        jobExecutionListener.afterJob(jobExecution); // 배치 작업 완료 후 관리자에게 알림 메일 전송

        return jobExecution; // 작업 실행 결과 반환
    }
}
