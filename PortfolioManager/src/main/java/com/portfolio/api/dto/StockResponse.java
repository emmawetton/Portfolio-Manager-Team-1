package com.portfolio.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockResponse {

    private Long id;
    private String name;
    private String symbol;
    private Double quantity;
    private Double purchasePrice;
    private LocalDate purchaseDate;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private Double profitLossPercentage;
}
