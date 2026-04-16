package com.portfolio.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class StockTrendResponse {

    private String symbol;
    private String name;
    private List<TrendPoint> trends;

    @Data
    public static class TrendPoint {
        private LocalDate date;
        private BigDecimal price;
    }
}
