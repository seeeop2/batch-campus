package com.fastcampus.batchcampus.batch.detail;

import com.fastcampus.batchcampus.domain.ApiOrder;
import com.fastcampus.batchcampus.domain.ServicePolicy;
import org.springframework.batch.item.ItemProcessor;

// ApiOrder 를 처리하여 Key 객체로 변환하는 프로세서 클래스
public class PreSettleDetailProcessor implements ItemProcessor<ApiOrder, Key> {

    // ApiOrder 아이템을 Key 객체로 변환하는 메서드
    @Override
    public Key process(ApiOrder item) throws Exception {
        // ApiOrder의 상태가 FAIL인 경우 null 반환 (해당 아이템은 처리하지 않음)
        if (item.getState() == ApiOrder.State.FAIL){
            return null;
        }

        // ApiOrder의 URL을 기반으로 ServicePolicy에서 서비스 ID를 찾음
        final Long serviceId = ServicePolicy.findByUrl(item.getUrl()).getId();

        // Key 객체 생성 및 반환: 고객 ID와 서비스 ID를 포함
        return new Key(
                item.getCustomerId(), // ApiOrder에서 고객 ID 가져오기
                serviceId // 찾은 서비스 ID
        );
    }
}
