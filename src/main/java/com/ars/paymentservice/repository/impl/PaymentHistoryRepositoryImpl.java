package com.ars.paymentservice.repository.impl;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.dto.response.FinanceStatisticDTO;
import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import com.ars.paymentservice.repository.PaymentHistoryRepositoryCustom;
import com.dct.config.common.SqlUtils;
import com.dct.model.dto.request.BaseRequestDTO;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepositoryCustom {
    private final EntityManager entityManager;

    public PaymentHistoryRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<PaymentHistoryDTO> getAllWithPaging(SearchPaymentHistoriesRequestDTO request) {
        String countSql = "SELECT COUNT(*) FROM payment_history p ";
        String querySql = """
            SELECT p.id, p.type, p.trans_id as transId, p.receiver_id as receiverId, p.user_id as userId,
            p.amount, p.status, p.description, p.payment_method as paymentMethod, p.payment_time as paymentTime
            FROM payment_history p
        """;

        Map<String, Object> params = new HashMap<>();
        StringBuilder whereConditions = new StringBuilder(SqlUtils.WHERE_DEFAULT);
        SqlUtils.addEqualCondition(whereConditions, params, "p.receiver_id", request.getShopId());
        SqlUtils.addEqualCondition(whereConditions, params, "p.status", request.getStatus());
        SqlUtils.addDateTimeCondition(whereConditions, params, request, "p.payment_time");
        SqlUtils.addLikeCondition(whereConditions, params, request.getKeyword(), "p.trans_id");
        SqlUtils.setOrderByDecreasing(whereConditions, "p.id");
        return SqlUtils.queryBuilder(entityManager)
                .querySql(querySql + whereConditions)
                .countQuerySql(countSql + whereConditions)
                .pageable(request.getPageable())
                .params(params)
                .getResultsWithPaging("paymentHistoriesGetWithPaging");
    }

    @Override
    public Optional<FinanceStatisticDTO> getFinanceStatisticForShop(Integer shopId, BaseRequestDTO request) {
        String revenueQuery = """
            SELECT
                amount AS revenue,
                0 AS platformFee
            FROM payment_history
            WHERE type = 2 AND receiver_id = :shopId AND status = 'SUCCESS'
        """;

        String platformFeeQuery = """
            SELECT
                0 AS revenue,
                amount AS platformFee
            FROM payment_history
            WHERE type = 3 AND user_id = :shopId AND status = 'SUCCESS'
        """;

        String querySql = """
            SELECT
                SUM(TempTable.revenue) AS totalRevenue,
                SUM(TempTable.platformFee) AS totalPlatformFee,
                SUM(TempTable.revenue - TempTable.platformFee) AS totalProfit
            FROM (
        """;

        Map<String, Object> params = new HashMap<>();
        StringBuilder whereConditions = new StringBuilder();
        SqlUtils.addDateTimeCondition(whereConditions, params, request, "payment_time");
        StringBuilder sql = new StringBuilder(querySql);
        params.put("shopId", shopId);
        sql.append(revenueQuery).append(whereConditions);
        sql.append(" UNION ALL ");
        sql.append(platformFeeQuery).append(whereConditions);
        sql.append(" ) AS TempTable ");
        return SqlUtils.queryBuilder(entityManager)
                .querySql(sql.toString())
                .params(params)
                .getSingleResult("getFinanceStatistic");
    }

    @Override
    public Optional<FinanceStatisticDTO> getFinanceStatisticForAdmin(BaseRequestDTO request) {
        String revenueQuery = """
            SELECT
                amount AS revenue,
                0 AS platformFee
            FROM payment_history
            WHERE type = 1 AND receiver_id = 0 AND status = 'SUCCESS'
        """;

        String platformFeeQuery = """
            SELECT
                0 AS revenue,
                amount AS platformFee
            FROM payment_history
            WHERE type = 3 AND user_id = 0 AND status = 'SUCCESS'
        """;

        String querySql = """
            SELECT
                SUM(TempTable.revenue) AS totalRevenue,
                SUM(TempTable.platformFee) AS totalProfit
            FROM (
        """;

        Map<String, Object> params = new HashMap<>();
        StringBuilder whereConditions = new StringBuilder();
        StringBuilder sql = new StringBuilder(querySql);
        SqlUtils.addDateTimeCondition(whereConditions, params, request, "payment_time");
        sql.append(revenueQuery).append(whereConditions);
        sql.append(" UNION ALL ");
        sql.append(platformFeeQuery).append(whereConditions);
        sql.append(" ) AS TempTable ");
        return SqlUtils.queryBuilder(entityManager)
                .querySql(sql.toString())
                .params(params)
                .getSingleResult("getFinanceStatistic");
    }
}
