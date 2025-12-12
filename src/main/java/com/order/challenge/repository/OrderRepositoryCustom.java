package com.order.challenge.repository;

import com.order.challenge.entities.OrderEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderRepositoryCustom {

    Mono<OrderEntity> findByOrderId(UUID orderId);

    Mono<OrderEntity> save(OrderEntity orderEntity);
}