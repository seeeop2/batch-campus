package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.batch.JobExecution;
import com.fastcampus.batchcampus.batch.JobExecutionListener;
import org.springframework.stereotype.Component;

// 배치 작업 실행 전후의 이벤트를 처리하는 리스너

@Component
public class DormantBatchJobExecutionListener implements JobExecutionListener {

    private final EmailProvider emailProvider; // 이메일 전송 기능 제공

    public DormantBatchJobExecutionListener() {
        this.emailProvider = new EmailProvider.Fake();
    }

    // 배치 작업 시작 전 호출되는 메서드
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // no-op
    }

    // 배치 작업 완료 후 호출되는 메서드
    @Override
    public void afterJob(JobExecution jobExecution) {
        // 관리자에게 배치 작업 완료 알림 이메일 전송
        emailProvider.send("admin@fastcampus.com", "배치 완료 알림", "DormantBatchJob이 수행되었습니다. status: " + jobExecution.getStatus());
    }
}
