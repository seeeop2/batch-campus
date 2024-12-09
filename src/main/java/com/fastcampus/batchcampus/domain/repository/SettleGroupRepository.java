package com.fastcampus.batchcampus.domain.repository;

import com.fastcampus.batchcampus.domain.SettleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SettleGroupRepository extends JpaRepository<SettleGroup, Long> {

    // 특정 기간 동안의 거래 내역을 기반으로 고객 ID와 서비스 ID로 그룹화하여 SettleGroup 생성
    @Query(
            value = """
                    SELECT new SettleGroup(detail.customerId, detail.serviceId, sum(detail.count), sum(detail.fee))
                    FROM SettleDetail  detail
                    WHERE detail.targetDate between :start and :end
                    AND detail.customerId = :customerId
                    GROUP BY detail.customerId, detail.serviceId
                    """
    )
    List<SettleGroup> findGroupByCustomerIdAndServiceId(LocalDate start, LocalDate end, Long customerId); // 그룹화된 결과 반환
}
