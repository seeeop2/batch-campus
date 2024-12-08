package com.fastcampus.batchcampus.batch.detail;

import com.fastcampus.batchcampus.domain.ServicePolicy;
import com.fastcampus.batchcampus.domain.SettleDetail;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SettleDetailProcessor implements ItemProcessor<KeyAndCount, SettleDetail>, StepExecutionListener {

    // 날짜 형식을 지정하기 위한 DateTimeFormatter
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private StepExecution stepExecution; // 현재 스텝의 실행 정보를 저장할 변수

    // KeyAndCount 아이템을 SettleDetail 객체로 변환하는 메서드
    @Override
    public SettleDetail process(KeyAndCount item) throws Exception {
        final Key key = item.key(); // Key 객체 가져오기
        final ServicePolicy servicePolicy = ServicePolicy.findById(key.serviceId()); // 서비스 ID를 통해 ServicePolicy 조회
        final Long count = item.count(); // 거래 수량 가져오기
        final String targetDate = stepExecution.getJobParameters().getString("targetDate"); // Job 파라미터에서 targetDate 가져오기

        // SettleDetail 객체 생성 및 반환
        return new SettleDetail(
                key.customerId(), // 고객 ID
                key.serviceId(), // 서비스 ID
                count, // 거래 수량
                servicePolicy.getFee() * count, // 수수료 계산
                LocalDate.parse(targetDate, dateTimeFormatter) // 거래 대상 날짜 파싱
        );
    }

    // 스텝 실행 전 호출되는 메서드: StepExecution 정보를 저장
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution; // 현재 스텝의 실행 정보를 저장
    }
}
