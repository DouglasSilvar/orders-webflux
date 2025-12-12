package com.order.challenge.repository.impl;

import com.mongodb.client.result.UpdateResult;
import com.order.challenge.entities.TotalMetricsEntity;
import com.order.challenge.repository.TotalMetricsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class TotalMetricsRepositoryImpl implements TotalMetricsRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;
    public static final String GLOBAL_ID = "GLOBAL_TOTAL";

    @Override
    public Mono<UpdateResult> incrementTotalValue(BigDecimal amount) {
        Query query = new Query(Criteria.where("_id").is(GLOBAL_ID));

        Update update = new Update().inc("totalValue", amount);

        return mongoTemplate.upsert(query, update, TotalMetricsEntity.class);
    }

    @Override
    public Mono<TotalMetricsEntity> findTotalMetrics() {
        Query query = new Query(Criteria.where("_id").is(GLOBAL_ID));
        return mongoTemplate.findOne(query, TotalMetricsEntity.class);
    }
}