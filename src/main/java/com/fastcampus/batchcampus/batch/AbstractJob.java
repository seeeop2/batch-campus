package com.fastcampus.batchcampus.batch;

import java.time.LocalDateTime;
import java.util.Objects;

// Job 인터페이스를 구현하는 추상 클래스
public abstract class AbstractJob implements Job {

    private final JobExecutionListener jobExecutionListener; // 작업 실행 전후 처리를 위한 리스너

    protected AbstractJob(JobExecutionListener jobExecutionListener) {
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
            doExecute();
            jobExecution.setStatus(BatchStatus.COMPLETED); // 배치 작업 완료 상태 설정
        } catch (Exception e){
            jobExecution.setStatus(BatchStatus.FAILED); // 예외 발생 시 실패 상태 설정
        }

        jobExecution.setEndTime(LocalDateTime.now()); // 종료 시간 기록

        jobExecutionListener.afterJob(jobExecution); // 배치 작업 완료 후 관리자에게 알림 메일 전송

        return jobExecution; // 작업 실행 결과 반환
    }

    // 서브클래스에서 구현해야 하는 추상 메서드
    public abstract void doExecute();

}
