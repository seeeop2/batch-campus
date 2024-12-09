package com.fastcampus.batchcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class SettleGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID
    private Long customerId; // 고객 ID
    private Long serviceId; // 서비스 ID
    private Long totalCount; // 총 거래 수량
    private Long totalFee; // 총 수수료
    private LocalDateTime createdAt; // 생성 날짜 및 시간

    public SettleGroup(Long customerId, Long serviceId, Long totalCount, Long totalFee) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.totalCount = totalCount;
        this.totalFee = totalFee;
        this.createdAt = LocalDateTime.now();
    }
}
