package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.mapping.RevenueDataMapping;
import com.ars.paymentservice.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {
    Optional<PaymentHistory> findByTypeAndRefId(Integer type, Integer refId);

    @Query(value = """
            SELECT
                DATE(payment_time) AS date,
                SUM(amount) AS amount
            FROM payment_history
            WHERE receiver_id = ?2
                AND type = ?1
                AND status = 'SUCCESS'
                AND payment_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(payment_time)
            ORDER BY date
        """,
        nativeQuery = true
    )
    List<RevenueDataMapping> getRevenueLastSevenDay(Integer type, Integer receiverId);

    @Query(value = """
            SELECT SUM(amount) AS amount
            FROM payment_history
            WHERE receiver_id = ?2
                AND type = ?1
                AND status = 'SUCCESS'
                AND payment_time >= CURDATE()
        """,
        nativeQuery = true
    )
    BigDecimal getRevenueToDay(Integer type, Integer receiverId);
}
