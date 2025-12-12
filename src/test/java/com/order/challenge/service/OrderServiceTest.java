package com.order.challenge.service;

import com.order.challenge.entities.OrderEntity;
import com.order.challenge.entities.TotalMetricsEntity;
import com.order.challenge.enums.OrderStatus;
import com.order.challenge.exception.OrderNotFoundException;
import com.order.challenge.exception.OrderPersistenceException;
import com.order.challenge.mapper.OrderMapper;
import com.order.challenge.repository.OrderRepositoryCustom;
import com.order.challenge.repository.TotalMetricsRepositoryCustom;
import com.order.challenge.request.OrderRequest;
import com.order.challenge.response.OrderResponse; // Importar OrderResponse
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepositoryCustom orderRepository;

    @Mock
    private TotalMetricsRepositoryCustom totalMetricsRepository;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;
    private OrderRequest mockRequest;
    private OrderEntity mockEntity;
    private OrderEntity mockSavedEntity;
    private OrderResponse mockResponse;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        BigDecimal price = new BigDecimal("10.00");
        Integer quantity = 5;
        BigDecimal totalValue = new BigDecimal("50.00");

        mockRequest = OrderRequest.builder()
                .orderId(orderId)
                .barCode("XYZ123")
                .quantity(quantity)
                .price(price)
                .build();

        mockEntity = OrderEntity.builder()
                .orderId(orderId)
                .barCode("XYZ123")
                .quantity(quantity)
                .price(price)
                .build();

        mockSavedEntity = OrderEntity.builder()
                .orderId(orderId)
                .barCode("XYZ123")
                .quantity(quantity)
                .price(price)
                .totalValue(totalValue)
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.NOT_ACCOUNTED)
                .build();

        // Inicializa o mockResponse
        mockResponse = OrderResponse.builder()
                .orderId(orderId)
                .barCode("XYZ123")
                .quantity(quantity)
                .price(price)
                .totalValue(totalValue)
                .status(OrderStatus.NOT_ACCOUNTED)
                .build();
    }

    @Test
    void createOrder_ShouldSaveOrderAndUpdateMetricsSuccessfully() {
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(mockEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(Mono.just(mockSavedEntity));
        when(totalMetricsRepository.incrementTotalValue(any(BigDecimal.class)))
                .thenReturn(Mono.just(UpdateResult.acknowledged(1, 1L, null)));

        // CORREÇÃO: Deve retornar um objeto OrderResponse
        when(orderMapper.toResponse(any(OrderEntity.class))).thenReturn(mockResponse);

        StepVerifier.create(orderService.createOrder(mockRequest))
                .expectNext(mockResponse) // Espera o objeto de resposta
                .verifyComplete();

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(totalMetricsRepository, times(1)).incrementTotalValue(mockSavedEntity.getTotalValue());
    }

    @Test
    void createOrder_ShouldThrowPersistenceException_WhenRepositorySaveFails() {
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(mockEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        StepVerifier.create(orderService.createOrder(mockRequest))
                .expectErrorMatches(throwable -> throwable instanceof OrderPersistenceException &&
                        throwable.getMessage().contains("Failed to save order with BarCode: XYZ123"))
                .verify();

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(totalMetricsRepository, never()).incrementTotalValue(any());
        // Não precisamos mockar o toResponse aqui, pois o fluxo falha antes.
    }

    @Test
    void getOrderById_ShouldReturnOrderResponse_WhenOrderExists() {
        when(orderRepository.findByOrderId(orderId)).thenReturn(Mono.just(mockEntity));

        // CORREÇÃO: Deve retornar um objeto OrderResponse
        when(orderMapper.toResponse(any(OrderEntity.class))).thenReturn(mockResponse);

        StepVerifier.create(orderService.getOrderById(orderId))
                .expectNext(mockResponse) // Espera o objeto de resposta
                .verifyComplete();

        verify(orderRepository, times(1)).findByOrderId(orderId);
        verify(orderMapper, times(1)).toResponse(mockEntity);
    }

    @Test
    void getOrderById_ShouldThrowNotFoundException_WhenOrderDoesNotExist() {
        when(orderRepository.findByOrderId(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrderById(orderId))
                .expectErrorMatches(throwable -> throwable instanceof OrderNotFoundException &&
                        throwable.getMessage().contains("Order not found with ID: " + orderId))
                .verify();

        verify(orderRepository, times(1)).findByOrderId(orderId);
        verify(orderMapper, never()).toResponse(any());
    }

    @Test
    void getTotalOrderValue_ShouldReturnTotalValue_WhenMetricsExist() {
        BigDecimal expectedTotal = new BigDecimal("1500.50");
        TotalMetricsEntity metrics = new TotalMetricsEntity("1", expectedTotal);

        when(totalMetricsRepository.findTotalMetrics()).thenReturn(Mono.just(metrics));

        StepVerifier.create(orderService.getTotalOrderValue())
                .expectNext(expectedTotal)
                .verifyComplete();
    }

    @Test
    void getTotalOrderValue_ShouldReturnZero_WhenMetricsDoNotExist() {
        when(totalMetricsRepository.findTotalMetrics()).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getTotalOrderValue())
                .expectNext(BigDecimal.ZERO)
                .verifyComplete();
    }
}