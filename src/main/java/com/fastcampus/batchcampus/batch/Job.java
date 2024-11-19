package com.fastcampus.batchcampus.batch;

public interface Job {
    // 배치 작업을 실행하는 메서드
    JobExecution execute();
}
