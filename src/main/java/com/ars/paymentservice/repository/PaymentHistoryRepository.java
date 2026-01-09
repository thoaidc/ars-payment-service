package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.mapping.RevenueDataMapping;
import com.ars.paymentservice.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer>, PaymentHistoryRepositoryCustom {
    Optional<PaymentHistory> findByTypeAndRefId(Integer type, Integer refId);

    @Query(value = """
            SELECT SUM(amount) AS amount, DATE(payment_time) AS date
            FROM payment_history
            WHERE ((receiver_id = :userId AND type = 2) OR (user_id = :userId AND type = 3))
            AND status = 'SUCCESS'
            AND payment_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(payment_time)
            ORDER BY date
        """,
        nativeQuery = true
    )
    List<RevenueDataMapping> getRevenueLastSevenDay(@Param("userId") Integer receiverId);

    @Query(value = """
            SELECT SUM(amount) AS amount, DATE(payment_time) AS date
            FROM payment_history
            WHERE type = 1
            AND status = 'SUCCESS'
            AND payment_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(payment_time)
            ORDER BY date
        """,
        nativeQuery = true
    )
    List<RevenueDataMapping> getRevenueLastSevenDayForAdmin();

    @Query(value = """
            SELECT SUM(amount) AS amount
            FROM payment_history
            WHERE ((receiver_id = :userId AND type = 2) OR (user_id = :userId AND type = 3))
            AND status = 'SUCCESS'
            AND payment_time >= CURDATE()
        """,
        nativeQuery = true
    )
    BigDecimal getRevenueToDayForShop(@Param("userId") Integer receiverId);

    @Query(value = """
            SELECT SUM(amount) AS amount
            FROM payment_history
            WHERE type = 1
            AND status = 'SUCCESS'
            AND payment_time >= CURDATE()
        """,
        nativeQuery = true
    )
    BigDecimal getRevenueToDayForAdmin();

    @Query(value = "SELECT p.info FROM payment_history p WHERE p.type = 1 AND p.ref_id = ? AND p.status = 'PENDING'", nativeQuery = true)
    Optional<String> getPaymentInfoByRefId(Integer orderId);
}
