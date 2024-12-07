package com.fastcampus.batchcampus.batch.detail;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Key 객체를 쓰기 위한 ItemWriter 구현 클래스
@Component
public class preSettleDetailWriter implements ItemWriter<Key>, StepExecutionListener {

    private StepExecution stepExecution; // 현재 스텝의 실행 정보를 저장할 변수

    // Chunk에 포함된 Key 객체를 처리하여 스냅샷 맵에 기록하는 메서드
    @Override
    public void write(Chunk<? extends Key> chunk) throws Exception {
        // ExecutionContext에서 "snapshots" 맵을 가져옴
        final ConcurrentMap<Key, Long> snapshotMap = (ConcurrentMap<Key, Long>) stepExecution.getExecutionContext().get("snapshots");

        // Chunk의 각 Key 객체에 대해 처리
        chunk.forEach(key -> {
            // Key 객체를 맵에 추가하거나 기존 값에 1을 더함
            snapshotMap.compute(
                    key,
                    (k, v) -> (v == null) ? 1 : v + 1 // Key가 존재하지 않으면 1로 초기화, 존재하면 1을 더함
            );
        });
    }

    // 스텝 실행 전 호출되는 메서드: ExecutionContext 초기화
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution; // 현재 스텝의 실행 정보를 저장

        // 새로운 ConcurrentHashMap을 생성하여 스냅샷 맵 초기화
        final ConcurrentMap<Key, Long> snapshotMap = new ConcurrentHashMap<>();
        stepExecution.getExecutionContext().put("snapshots", snapshotMap); // ExecutionContext에 저장
    }
}
