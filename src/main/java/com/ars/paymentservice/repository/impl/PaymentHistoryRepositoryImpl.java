package com.ars.paymentservice.repository.impl;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import com.ars.paymentservice.repository.PaymentHistoryRepositoryCustom;
import com.dct.config.common.SqlUtils;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

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
}
