package com.intership.paymentservice.repository.impl;

import com.intership.paymentservice.model.entity.Payment;
import com.intership.paymentservice.repository.PaymentRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Payment save(Payment payment) {
        return mongoTemplate.save(payment);
    }

    @Override
    public List<Payment> findByOrderId(Long orderId) {
        Query query = new Query(Criteria.where("order_id").is(orderId));
        return mongoTemplate.find(query, Payment.class);
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        Query query = new Query(Criteria.where("user_id").is(userId));
        return mongoTemplate.find(query, Payment.class);
    }

    @Override
    public Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId) {
        Query query = new Query(Criteria.where("order_id").is(orderId).and("userId").is(userId));
        return Optional.ofNullable(
                mongoTemplate.findOne(query, Payment.class)
        );
    }

    @Override
    public List<Payment> findByStatusIn(List<Payment.Status> statuses) {
        Query query = new Query(Criteria.where("status").in(statuses));
        return mongoTemplate.find(query, Payment.class);
    }

    @Override
    public BigDecimal getTotalPaymentsAmountForPeriod(Timestamp startDate, Timestamp endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("timestamp").gte(startDate).lte(endDate)),
                group().sum("payment_amount").as("totalAmount")
        );

        AggregationResults<TotalAmountResult> results =
                mongoTemplate.aggregate(aggregation, "payments", TotalAmountResult.class);

        TotalAmountResult totalAmountResult = results.getUniqueMappedResult();
        return totalAmountResult != null ? totalAmountResult.getTotalAmount() : BigDecimal.ZERO;
    }

    @Setter
    @Getter
    private static class TotalAmountResult {
        private BigDecimal totalAmount;
    }
}