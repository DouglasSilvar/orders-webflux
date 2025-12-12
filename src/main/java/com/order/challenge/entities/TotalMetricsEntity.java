package com.order.challenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "total_metrics")
public class TotalMetricsEntity {

    @Id
    private String id = "GLOBAL_TOTAL";

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalValue;
}