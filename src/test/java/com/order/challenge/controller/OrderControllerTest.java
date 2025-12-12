package com.order.challenge.controller;

import com.order.challenge.request.OrderRequest;
import com.order.challenge.response.OrderResponse;
import com.order.challenge.service.OrderService;
import com.order.challenge.enums.OrderStatus;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;


    private final UUID ORDER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
    private final OrderRequest mockOrderRequest = OrderRequest.builder()
            .barCode("4567890")
            .quantity(2)
            .price(new BigDecimal("10.50"))
            .build();
    private final OrderResponse mockOrderResponse = OrderResponse.builder()
            .orderId(ORDER_ID)
            .barCode("4567890")
            .quantity(2)
            .price(new BigDecimal("10.50"))
            .totalValue(new BigDecimal("21.00"))
            .createdAt(LocalDateTime.now())
            .status(OrderStatus.NOT_ACCOUNTED)
            .build();

    /**
     * Teste para o endpoint POST /v1/api/orders (createOrder).
     */
    @Test
    void createOrder_ShouldReturnCreatedOrder_AndStatus201() {

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenReturn(Mono.just(mockOrderResponse));

        webTestClient.post().uri("/v1/api/orders")
                .contentType(MediaType.APPLICATION_JSON) // Define o cabeçalho como JSON
                .body(Mono.just(mockOrderRequest), OrderRequest.class) // Envia o body no formato Mono
                .exchange() // Executa a requisição
                .expectStatus().isCreated() // Verifica se o status HTTP é 201 CREATED
                .expectBody(OrderResponse.class) // Espera que o corpo seja OrderResponse
                .isEqualTo(mockOrderResponse); // Verifica se o objeto retornado é o esperado
    }

    @Test
    void getOrderById_ShouldReturnOrder_AndStatus200() {
        when(orderService.getOrderById(ORDER_ID))
                .thenReturn(Mono.just(mockOrderResponse));

        webTestClient.get().uri("/v1/api/orders/{orderId}", ORDER_ID)
                .accept(MediaType.APPLICATION_JSON) // Define o cabeçalho de aceitação
                .exchange()
                .expectStatus().isOk() // Verifica se o status HTTP é 200 OK
                .expectBody(OrderResponse.class)
                .isEqualTo(mockOrderResponse);
    }

    @Test
    void getTotalOrderValue_ShouldReturnTotalValue_AndStatus200() {
        final BigDecimal TOTAL_VALUE = new BigDecimal("150.75");
        when(orderService.getTotalOrderValue())
                .thenReturn(Mono.just(TOTAL_VALUE));

        // 2. Ação e Verificação
        webTestClient.get().uri("/v1/api/orders/total-value")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk() // Verifica se o status HTTP é 200 OK
                .expectBody(BigDecimal.class) // Espera que o corpo seja um BigDecimal
                .isEqualTo(TOTAL_VALUE);
    }
}