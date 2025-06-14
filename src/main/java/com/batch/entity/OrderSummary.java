package com.batch.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSummary {
    private Integer id;

    private Integer customerId;

    private String itemName;

    private Integer itemPrice;
}
