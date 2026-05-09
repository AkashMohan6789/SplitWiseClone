package com.splitwise.clone.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExpenseRequest {
    private Long groupId;
    private String description;
    private BigDecimal amount;
    private Long paidById;
    // Map of userId to amount they owe
    private Map<Long, BigDecimal> splits;
}
