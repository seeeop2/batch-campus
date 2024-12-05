package com.fastcampus.batchcampus.batch.generator;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@StepScope // StepScope 어노테이션: Step의 실행 컨텍스트에 따라 빈 생성
@Component
public class ApiOrderGenerateReader implements ItemReader<Boolean> {

    private Long totalCount; // 총 생성할 주문 수
    private AtomicLong current; // 현재까지 생성된 주문 수를 관리하는 AtomicLong

    // 생성자: Job 파라미터에서 totalCount를 읽어 초기화
    public ApiOrderGenerateReader(@Value("#{jobParameters['totalCount']}") String totalCount){
        this.totalCount = Long.valueOf(totalCount); // 문자열을 Long으로 변환하여 저장
        this.current = new AtomicLong(0); // 현재 주문 수 초기화
    }

    // ItemReader 인터페이스의 read 메서드 구현
    @Override
    public Boolean read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // 현재 주문 수를 증가시키고, 총 주문 수를 초과하면 null 반환
        if (current.incrementAndGet() > totalCount){
            return null; // 더 이상 읽을 데이터가 없음을 나타냄
        }
        return true; // 주문을 생성할 수 있음을 나타냄
    }
}
