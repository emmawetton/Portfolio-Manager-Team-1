package com.portfolio.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PortfolioSummaryResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate createdDate;
    private BigDecimal totalValue;
    private BigDecimal totalProfitLoss;
    private Double totalProfitLossPercentage;
    private int numberOfStocks;
    private List<StockResponse> stocks;
}