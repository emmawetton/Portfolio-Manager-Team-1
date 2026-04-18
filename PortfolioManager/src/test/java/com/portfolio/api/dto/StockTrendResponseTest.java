package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StockTrendResponseTest {

    @Test
    void testFieldsAndGettersSetters() {
        StockTrendResponse response = new StockTrendResponse();

        String symbol = "AAPL";
        String name = "Apple Inc.";

        StockTrendResponse.TrendPoint point1 =
                new StockTrendResponse.TrendPoint("2024-01-01", new BigDecimal("150.00"));
        StockTrendResponse.TrendPoint point2 =
                new StockTrendResponse.TrendPoint("2024-01-02", new BigDecimal("152.50"));

        List<StockTrendResponse.TrendPoint> trends = new ArrayList<>();
        trends.add(point1);
        trends.add(point2);

        response.setSymbol(symbol);
        response.setName(name);
        response.setTrends(trends);

        assertEquals(symbol, response.getSymbol());
        assertEquals(name, response.getName());
        assertEquals(2, response.getTrends().size());
        assertEquals("2024-01-01", response.getTrends().get(0).getDate());
        assertEquals(new BigDecimal("152.50"), response.getTrends().get(1).getPrice());
    }

    @Test
    void testTrendPointGettersSetters() {
        StockTrendResponse.TrendPoint point = new StockTrendResponse.TrendPoint();

        String date = "2024-01-10";
        BigDecimal price = new BigDecimal("175.25");

        point.setDate(date);
        point.setPrice(price);

        assertEquals(date, point.getDate());
        assertEquals(price, point.getPrice());
    }

    @Test
    void testEqualsAndHashCode() {
        StockTrendResponse r1 = new StockTrendResponse();
        r1.setSymbol("AAPL");

        StockTrendResponse r2 = new StockTrendResponse();
        r2.setSymbol("AAPL");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testTrendPointEqualsAndHashCode() {
        StockTrendResponse.TrendPoint p1 =
                new StockTrendResponse.TrendPoint("2024-01-01", new BigDecimal("150.00"));

        StockTrendResponse.TrendPoint p2 =
                new StockTrendResponse.TrendPoint("2024-01-01", new BigDecimal("150.00"));

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        StockTrendResponse response = new StockTrendResponse();
        response.setSymbol("AAPL");

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("AAPL"));
    }

    @Test
    void testTrendPointToStringNotNull() {
        StockTrendResponse.TrendPoint point =
                new StockTrendResponse.TrendPoint("2024-01-01", new BigDecimal("150.00"));

        assertNotNull(point.toString());
        assertTrue(point.toString().contains("2024-01-01"));
    }
}
