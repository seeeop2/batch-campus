package com.fastcampus.batchcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@NoArgsConstructor
@Entity
public class SettleDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자
    private Long customerId; // 고객 ID
    private Long serviceId; // 서비스 ID
    private Long count; // 거래 수량
    private Long fee; // 수수료
    private LocalDate targetDate; // 거래 대상 날짜

    public SettleDetail(Long customerId, Long serviceId, Long count, Long fee, LocalDate targetDate) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.count = count;
        this.fee = fee;
        this.targetDate = targetDate;
    }
}
