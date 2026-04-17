package com.portfolio.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StockTrendResponse {

    private String symbol;
    private String name;
    private List<TrendPoint> trends;

    @Data
    public static class TrendPoint {
        private String date;
        private BigDecimal price;

        public TrendPoint(String date, BigDecimal price) {
            this.date = date;
            this.price = price;
        }
    }
}