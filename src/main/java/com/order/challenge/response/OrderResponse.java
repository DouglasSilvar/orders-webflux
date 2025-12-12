package com.order.challenge.response;

import com.order.challenge.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID orderId;
    private String barCode;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalValue;
    private LocalDateTime createdAt;
    private OrderStatus status;
}
