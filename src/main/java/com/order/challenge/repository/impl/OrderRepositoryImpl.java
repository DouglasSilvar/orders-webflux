package com.order.challenge.repository.impl;

import com.order.challenge.entities.OrderEntity;
import com.order.challenge.repository.OrderRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<OrderEntity> findByOrderId(UUID orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        return mongoTemplate.findOne(query, OrderEntity.class);
    }

    @Override
    public Mono<OrderEntity> save(OrderEntity orderEntity) {
        return mongoTemplate.save(orderEntity);
    }
}