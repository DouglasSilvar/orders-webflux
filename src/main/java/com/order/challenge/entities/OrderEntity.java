package com.order.challenge.entities;

import com.order.challenge.enums.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class OrderEntity {

    @Id
    private UUID orderId;
    private String barCode;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalValue;
    private LocalDateTime createdAt;
    private OrderStatus status = OrderStatus.NOT_ACCOUNTED;


}
