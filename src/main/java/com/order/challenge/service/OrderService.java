package com.order.challenge.service;

import com.order.challenge.entities.OrderEntity;
import com.order.challenge.entities.TotalMetricsEntity;
import com.order.challenge.exception.OrderNotFoundException;
import com.order.challenge.exception.OrderPersistenceException;
import com.order.challenge.mapper.OrderMapper;
import com.order.challenge.repository.OrderRepositoryCustom;
import com.order.challenge.request.OrderRequest;
import com.order.challenge.response.OrderResponse;
import com.order.challenge.repository.TotalMetricsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {


    private final OrderMapper orderMapper;
    private final OrderRepositoryCustom orderRepository;
    private final TotalMetricsRepositoryCustom totalMetricsRepository;

    public Mono<OrderResponse> createOrder(OrderRequest request) {

        log.info("Received request to create a new order. BarCode: {}", request.getBarCode());
        OrderEntity orderEntityToPersist = orderMapper.toEntity(request);

        BigDecimal totalValue = orderEntityToPersist.getPrice().multiply(new BigDecimal(orderEntityToPersist.getQuantity()));

        orderEntityToPersist.setOrderId(request.getOrderId());
        orderEntityToPersist.setTotalValue(totalValue);
        orderEntityToPersist.setCreatedAt(LocalDateTime.now());

        log.info("Total value calculated for the order (BarCode: {}): {}",
                orderEntityToPersist.getBarCode(), totalValue);

        return orderRepository.save(orderEntityToPersist)
                .onErrorMap(throwable -> {
                    String errorMessage = String.format("Failed to save order with BarCode: %s", request.getBarCode());
                    return new OrderPersistenceException(errorMessage, throwable);
                })
                .flatMap(savedOrder -> {
                    log.info("Order successfully saved. Starting total metrics update.");

                    return totalMetricsRepository.incrementTotalValue(savedOrder.getTotalValue())
                            .doOnNext(updateResult -> {
                                if (updateResult != null) {
                                    log.debug("Metrics updated. Matched: {}, Modified: {}",
                                            updateResult.getMatchedCount(),
                                            updateResult.getModifiedCount());
                                } else {
                                    log.warn("Metrics update succeeded but returned a null UpdateResult.");
                                }
                            })
                            .thenReturn(savedOrder);
                })
                .map(orderMapper::toResponse)
                .doOnError(error -> log.error("Persistence failed. BarCode: {}. Error: {}", request.getBarCode(), error.getMessage()));
    }

    public Mono<OrderResponse> getOrderById(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .switchIfEmpty(Mono.error(() -> new OrderNotFoundException("Order not found with ID: " + orderId)))
                .map(orderMapper::toResponse);
    }

    public Mono<BigDecimal> getTotalOrderValue() {
        return totalMetricsRepository.findTotalMetrics()
                .map(TotalMetricsEntity::getTotalValue)
                .defaultIfEmpty(BigDecimal.ZERO);
    }
}