package com.order.challenge.controller;

import com.order.challenge.request.OrderRequest;
import com.order.challenge.response.OrderResponse;
import com.order.challenge.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderResponse> createOrder(@RequestBody Mono<OrderRequest> orderRequestMono) {
        return orderRequestMono.flatMap(orderService::createOrder);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/total-value")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BigDecimal> getTotalOrderValue() {
        return orderService.getTotalOrderValue();
    }
}