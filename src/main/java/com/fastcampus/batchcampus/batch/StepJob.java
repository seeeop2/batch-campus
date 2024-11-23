package com.fastcampus.batchcampus.batch;

import java.util.List;

// 여러 Step을 포함하는 배치 작업을 정의하는 StepJob 클래스
public class StepJob extends AbstractJob {

    private final List<Step> steps; // 실행할 Step 목록

    public StepJob(List<Step> steps, JobExecutionListener jobExecutionListener) {
        super(jobExecutionListener); // 부모 클래스의 생성자 호출
        this.steps = steps; // 전달받은 Step 목록 저장
    }

    // 배치 작업의 실행 로직을 정의하는 메서드
    @Override
    public void doExecute() {
        steps.forEach(Step::execute); // 각 Step을 순회하며 실행
    }
}
