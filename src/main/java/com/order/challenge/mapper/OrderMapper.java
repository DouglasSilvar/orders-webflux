package com.order.challenge.mapper;

import com.order.challenge.entities.OrderEntity;
import com.order.challenge.request.OrderRequest;
import com.order.challenge.response.OrderResponse;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class OrderMapper {

    public OrderResponse toResponse(OrderEntity orderEntity) {
        if (Objects.isNull(orderEntity)) {
            return null;
        }

        return OrderResponse.builder()
                .orderId(orderEntity.getOrderId())
                .barCode(orderEntity.getBarCode())
                .quantity(orderEntity.getQuantity())
                .price(orderEntity.getPrice())
                .totalValue(orderEntity.getTotalValue())
                .createdAt(orderEntity.getCreatedAt())
                .status(orderEntity.getStatus())
                .build();
    }

    public OrderEntity toEntity(OrderRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }

        return OrderEntity.builder()
                .orderId(request.getOrderId())
                .barCode(request.getBarCode())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();
    }
}