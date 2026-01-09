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
            WITH RECURSIVE dates AS (
                SELECT DATE_SUB(CURDATE(), INTERVAL 6 DAY) AS calendar_date
                UNION ALL
                SELECT calendar_date + INTERVAL 1 DAY
                FROM dates
                WHERE calendar_date < CURDATE()
            )
            SELECT
                d.calendar_date AS date,
                COALESCE(SUM(p.amount), 0) AS amount
            FROM dates d
            LEFT JOIN payment_history p ON DATE(p.payment_time) = d.calendar_date
                AND p.status = 'SUCCESS'
                AND ((p.receiver_id = :userId AND p.type = 2) OR (p.user_id = :userId AND p.type = 3))
            GROUP BY d.calendar_date
            ORDER BY d.calendar_date
        """,
        nativeQuery = true
    )
    List<RevenueDataMapping> getRevenueLastSevenDay(@Param("userId") Integer receiverId);

    @Query(value = """
            WITH RECURSIVE dates AS (
                SELECT DATE_SUB(CURDATE(), INTERVAL 6 DAY) AS calendar_date
                UNION ALL
                SELECT calendar_date + INTERVAL 1 DAY
                FROM dates
                WHERE calendar_date < CURDATE()
            )
            SELECT
                d.calendar_date AS date,
                COALESCE(SUM(p.amount), 0) AS amount
            FROM dates d
            LEFT JOIN payment_history p ON DATE(p.payment_time) = d.calendar_date
                AND p.type = 1
                AND p.status = 'SUCCESS'
            GROUP BY d.calendar_date
            ORDER BY d.calendar_date
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
