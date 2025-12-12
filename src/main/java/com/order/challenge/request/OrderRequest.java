package com.order.challenge.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "The orderId must be provided.")
    private UUID orderId;

    @NotBlank(message = "The barCode must be provided.")
    private String barCode;

    @NotNull(message = "The quantity must be provided.")
    @Min(value = 1, message = "The quantity must be at least 1.")
    private Integer quantity;

    @NotNull(message = "The price must be provided.")
    @DecimalMin(value = "0.01", inclusive = true, message = "The price must be greater than zero.")
    private BigDecimal price;
}
