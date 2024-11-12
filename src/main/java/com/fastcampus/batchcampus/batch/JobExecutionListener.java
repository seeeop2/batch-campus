package com.fastcampus.batchcampus.batch;

// 배치 작업 실행 전후의 이벤트를 처리하는 리스너

public interface JobExecutionListener {

    void beforeJob(JobExecution jobExecution);

    void afterJob(JobExecution jobExecution);

}
