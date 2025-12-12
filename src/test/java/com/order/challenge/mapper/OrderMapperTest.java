package com.order.challenge.mapper;

import com.order.challenge.entities.OrderEntity;
import com.order.challenge.enums.OrderStatus;
import com.order.challenge.request.OrderRequest;
import com.order.challenge.response.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;
    private UUID orderId;
    private OrderRequest mockRequest;
    private OrderEntity mockEntity;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        orderId = UUID.randomUUID();

        mockRequest = OrderRequest.builder()
                .orderId(orderId)
                .barCode("4567890")
                .quantity(3)
                .price(new BigDecimal("15.00"))
                .build();

        mockEntity = OrderEntity.builder()
                .orderId(orderId)
                .barCode("4567890")
                .quantity(3)
                .price(new BigDecimal("15.00"))
                .totalValue(new BigDecimal("45.00"))
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.NOT_ACCOUNTED)
                .build();
    }

    @Test
    void toResponse_ShouldConvertEntityToResponseCorrectly() {
        OrderResponse response = orderMapper.toResponse(mockEntity);

        assertNotNull(response);
        assertEquals(mockEntity.getOrderId(), response.getOrderId());
        assertEquals(mockEntity.getBarCode(), response.getBarCode());
        assertEquals(mockEntity.getQuantity(), response.getQuantity());
        assertEquals(mockEntity.getPrice(), response.getPrice());
        assertEquals(mockEntity.getTotalValue(), response.getTotalValue());
        assertEquals(mockEntity.getCreatedAt(), response.getCreatedAt());
        assertEquals(mockEntity.getStatus(), response.getStatus());
    }

    @Test
    void toResponse_ShouldReturnNull_WhenEntityIsNull() {
        OrderResponse response = orderMapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toEntity_ShouldConvertRequestToEntityCorrectly() {
        OrderEntity entity = orderMapper.toEntity(mockRequest);

        assertNotNull(entity);
        assertEquals(mockRequest.getOrderId(), entity.getOrderId());
        assertEquals(mockRequest.getBarCode(), entity.getBarCode());
        assertEquals(mockRequest.getQuantity(), entity.getQuantity());
        assertEquals(mockRequest.getPrice(), entity.getPrice());

        assertNull(entity.getTotalValue());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getStatus());
    }

    @Test
    void toEntity_ShouldReturnNull_WhenRequestIsNull() {
        OrderEntity entity = orderMapper.toEntity(null);

        assertNull(entity);
    }
}