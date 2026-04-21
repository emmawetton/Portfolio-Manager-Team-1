package com.portfolio.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class StockTrendResponse {

    private String symbol;
    private String name;
    private List<TrendPoint> trends;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrendPoint {
        private String date;
        private BigDecimal price;
    }
}