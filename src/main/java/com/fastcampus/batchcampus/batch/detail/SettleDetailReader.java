package com.fastcampus.batchcampus.batch.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
class SettleDetailReader implements ItemReader<KeyAndCount>, StepExecutionListener {

    private Iterator<Map.Entry<Key, Long>> iterator; // Key와 Long 값을 포함하는 이터레이터

    // 데이터 읽기 메서드: KeyAndCount 객체를 반환
    @Override
    public KeyAndCount read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // 이터레이터에 다음 요소가 없으면 null 반환 (읽을 데이터가 없음을 의미)
        if (!iterator.hasNext()) {
            return null;
        }

        final Map.Entry<Key, Long> map = iterator.next(); // 이터레이터에서 다음 요소 가져오기

        return new KeyAndCount(map.getKey(), map.getValue()); // KeyAndCount 객체 생성 및 반환
    }

    // 스텝 실행 전 호출되는 메서드: 이터레이터 초기화
    @Override
    public void beforeStep(StepExecution stepExecution) {
        final JobExecution jobExecution = stepExecution.getJobExecution(); // 현재 JobExecution 가져오기

        // ExecutionContext에서 "snapshots" 맵을 가져와 이터레이터 초기화
        final Map<Key, Long> snapshots = (ConcurrentHashMap<Key, Long>) jobExecution.getExecutionContext().get("snapshots");
        iterator = snapshots.entrySet().iterator(); // 이터레이터를 맵의 엔트리 세트로 초기화
    }
}
