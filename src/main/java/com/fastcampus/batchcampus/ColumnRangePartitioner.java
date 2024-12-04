package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ColumnRangePartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;

    public ColumnRangePartitioner(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // partition 메서드: 주어진 gridSize에 따라 데이터 분할
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        // USER 테이블에서 최소 및 최대 ID 조회
        final Integer min = jdbcTemplate.queryForObject("SELECT MIN(id) from USER", Integer.class);
        final Integer max = jdbcTemplate.queryForObject("SELECT MAX(id) from USER", Integer.class);

        // 각 파티션의 크기 계산
        int targetSize = (max - min) / gridSize + 1;

        final Map<String, ExecutionContext> result = new HashMap<>(); // 결과를 저장할 맵
        int number = 0; // 파티션 번호
        int start = min; // 시작 ID
        int end = start + targetSize - 1; // 종료 ID

        // 주어진 범위를 기반으로 파티션 생성
        while (start <= max) {
            final ExecutionContext value = new ExecutionContext(); // 새로운 ExecutionContext 생성
            result.put("partition" + number, value); // 파티션 번호를 키로 하여 ExecutionContext 저장

            // 종료 ID가 최대 ID를 초과하지 않도록 설정
            if (end >= max) {
                end = max;
            }

            // 시작 및 종료 ID를 ExecutionContext에 저장
            value.putInt("minValue", start);
            value.putInt("maxValue", end);

            // 다음 파티션을 위해 시작 및 종료 ID 갱신
            start += targetSize;
            end += targetSize;
            number++; // 파티션 번호 증가
        }

        return result; // 생성된 파티션 맵 반환
    }

    // 예시:
    // partition0: 1, 20
    // partition1: 21, 40
    // partition2: 41, 60
    // ...
}
