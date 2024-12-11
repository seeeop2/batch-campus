package com.fastcampus.batchcampus.batch.group;

import com.fastcampus.batchcampus.domain.Customer;
import com.fastcampus.batchcampus.domain.SettleGroup;
import com.fastcampus.batchcampus.domain.repository.SettleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// SettleGroupProcessor 클래스: 고객 정보를 처리하여 SettleGroup 리스트를 생성하는 ItemProcessor 구현

@RequiredArgsConstructor
@Component
public class SettleGroupProcessor implements ItemProcessor<Customer, List<SettleGroup>>, StepExecutionListener {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 날짜 형식 지정
    private final SettleGroupRepository settleGroupRepository; // SettleGroup 데이터 접근을 위한 리포지토리
    private StepExecution stepExecution; // 현재 StepExecution 정보 저장

    // 고객 정보를 처리하여 SettleGroup 리스트를 반환하는 메서드
    @Override
    public List<SettleGroup> process(Customer item) throws Exception {
        // JobParameters에서 targetDate를 가져와 LocalDate로 변환
        final String targetDate = stepExecution.getJobParameters().getString("targetDate");
        final LocalDate end = LocalDate.parse(targetDate, formatter); // 종료일 설정

        // 고객 ID에 따라 거래 내역을 조회하여 SettleGroup 리스트 반환
        return settleGroupRepository.findGroupByCustomerIdAndServiceId(
                end.minusDays(6), // 시작일: 종료일로부터 6일 전
                end, // 종료일
                item.getId() // 고객 ID
        );
    }

    // StepExecution이 시작되기 전에 호출되는 메서드
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution; // StepExecution 정보 저장
    }
}
