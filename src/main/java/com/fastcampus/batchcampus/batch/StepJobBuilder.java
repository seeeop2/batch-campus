package com.fastcampus.batchcampus.batch;

import java.util.ArrayList;
import java.util.List;

// StepJob 객체를 구성하기 위한 빌더 클래스
public class StepJobBuilder {
    private final List<Step> steps; // 배치 작업에 포함될 Step 목록
    private JobExecutionListener jobExecutionListener; // 작업 실행 리스너

    public StepJobBuilder() {
        this.steps = new ArrayList<>();
    }

    // 첫 번째 Step을 설정하거나 기존의 첫 번째 Step을 교체하는 메서드
    public StepJobBuilder start(Step step) {
        if(steps.isEmpty()) {
            steps.add(step); // 목록이 비어있으면 Step 추가
        } else {
            steps.set(0, step); // 목록이 비어있지 않으면 첫 번째 Step 교체
        }
        return this; // 빌더 자신을 반환하여 메서드 체이닝 가능
    }

    // 다음 Step을 추가하는 메서드
    public StepJobBuilder next(Step step) {
        steps.add(step); // Step 목록에 Step 추가
        return this; // 빌더 자신을 반환하여 메서드 체이닝 가능
    }

    // JobExecutionListener를 설정하는 메서드
    public StepJobBuilder listener(JobExecutionListener jobExecutionListener) {
        this.jobExecutionListener = jobExecutionListener; // 리스너 저장
        return this; // 빌더 자신을 반환하여 메서드 체이닝 가능
    }

    // 설정된 Step 목록과 리스너를 사용하여 StepJob 객체를 생성하는 메서드
    public StepJob build() {
        return new StepJob(steps, jobExecutionListener); // StepJob 객체 생성 및 반환
    }
}
