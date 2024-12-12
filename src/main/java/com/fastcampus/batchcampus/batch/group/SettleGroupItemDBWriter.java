package com.fastcampus.batchcampus.batch.group;

import com.fastcampus.batchcampus.domain.SettleGroup;
import com.fastcampus.batchcampus.domain.repository.SettleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// SettleGroupItemDBWriter 클래스: SettleGroup 리스트를 데이터베이스에 저장하는 ItemWriter 구현

@RequiredArgsConstructor
@Component
public class SettleGroupItemDBWriter implements ItemWriter<List<SettleGroup>> {

    private final SettleGroupRepository settleGroupRepository; // SettleGroup 데이터 저장을 위한 레포지토리

    // Chunk를 받아 SettleGroup 리스트를 데이터베이스에 저장하는 메서드
    @Override
    public void write(Chunk<? extends List<SettleGroup>> chunk) throws Exception {
        final List<SettleGroup> settleGroups = new ArrayList<>(); // 저장할 SettleGroup 리스트 초기화

        chunk.forEach(settleGroups::addAll); // Chunk의 각 요소를 settleGroups 리스트에 추가
        settleGroupRepository.saveAll(settleGroups); // 모든 SettleGroup을 데이터베이스에 저장
    }
}
