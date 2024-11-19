package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.customer.Customer;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

// 배치 작업을 실행하는 Job 클래스

public class TaskletJob implements Job {

    private final Tasklet tasklet; // 실행할 작업
    private final JobExecutionListener jobExecutionListener; // 작업 실행 전후 처리를 위한 리스너

    // Tasklet만을 받는 생성자
    public TaskletJob(Tasklet tasklet) {
        this(tasklet, null);
    }

    // Builder 패턴을 사용하는 생성자: ItemReader, ItemProcessor, ItemWriter를 받아 SimpleTasklet 생성
    @Builder
    public TaskletJob(ItemReader<Customer> itemReader, ItemProcessor<Customer,Customer> itemProcessor, ItemWriter<Customer> itemWriter, JobExecutionListener jobExecutionListener) {
        this (new SimpleTasklet(itemReader, itemProcessor, itemWriter), jobExecutionListener);
    }

    // 생성자: CustomerRepository 주입 및 Fake EmailProvider 초기화
    public TaskletJob(Tasklet tasklet, JobExecutionListener jobExecutionListener) {
        this.tasklet = tasklet;

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

    // 배치 작업을 실행하는 메서드
    @Override
    public JobExecution execute(){

        final JobExecution jobExecution = new JobExecution();
        jobExecution.setStatus(BatchStatus.STARTING); // 배치 작업 시작 상태 설정
        jobExecution.setStartTime(LocalDateTime.now()); // 시작 시간 기록

        jobExecutionListener.beforeJob(jobExecution); // 전처리

        try {
            tasklet.execute();

            jobExecution.setStatus(BatchStatus.COMPLETED); // 배치 작업 완료 상태 설정
            jobExecutionListener.afterJob(jobExecution); // 후처리

        } catch (Exception e){
            jobExecution.setStatus(BatchStatus.FAILED); // 예외 발생 시 실패 상태 설정
        }

        jobExecution.setEndTime(LocalDateTime.now()); // 종료 시간 기록

        jobExecutionListener.afterJob(jobExecution); // 배치 작업 완료 후 관리자에게 알림 메일 전송

        return jobExecution;
    }

}
