package com.order.challenge.repository;

import com.mongodb.client.result.UpdateResult;
import com.order.challenge.entities.TotalMetricsEntity;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TotalMetricsRepositoryCustom {
    Mono<UpdateResult> incrementTotalValue(BigDecimal amount);

    Mono<TotalMetricsEntity> findTotalMetrics();
}