package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class StockResponseTest {

    @Test
    void testFieldsAndGettersSetters() {
        StockResponse response = new StockResponse();

        Long id = 1L;
        String name = "Apple Inc.";
        String symbol = "AAPL";
        Double quantity = 10.0;
        Double purchasePrice = 150.0;
        LocalDate purchaseDate = LocalDate.of(2024, 1, 10);
        BigDecimal currentPrice = new BigDecimal("175.50");
        BigDecimal currentValue = new BigDecimal("1755.00");
        BigDecimal profitLoss = new BigDecimal("255.00");
        Double profitLossPercentage = 17.0;

        response.setId(id);
        response.setName(name);
        response.setSymbol(symbol);
        response.setQuantity(quantity);
        response.setPurchasePrice(purchasePrice);
        response.setPurchaseDate(purchaseDate);
        response.setCurrentPrice(currentPrice);
        response.setCurrentValue(currentValue);
        response.setProfitLoss(profitLoss);
        response.setProfitLossPercentage(profitLossPercentage);

        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(symbol, response.getSymbol());
        assertEquals(quantity, response.getQuantity());
        assertEquals(purchasePrice, response.getPurchasePrice());
        assertEquals(purchaseDate, response.getPurchaseDate());
        assertEquals(currentPrice, response.getCurrentPrice());
        assertEquals(currentValue, response.getCurrentValue());
        assertEquals(profitLoss, response.getProfitLoss());
        assertEquals(profitLossPercentage, response.getProfitLossPercentage());
    }

    @Test
    void testEqualsAndHashCode() {
        StockResponse r1 = new StockResponse();
        r1.setId(1L);
        r1.setSymbol("AAPL");

        StockResponse r2 = new StockResponse();
        r2.setId(1L);
        r2.setSymbol("AAPL");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        StockResponse response = new StockResponse();
        response.setSymbol("AAPL");

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("AAPL"));
    }

    @Test
    void testNumericFields() {
        StockResponse response = new StockResponse();

        response.setQuantity(5.0);
        response.setPurchasePrice(120.0);
        response.setProfitLossPercentage(10.5);

        assertEquals(5.0, response.getQuantity());
        assertEquals(120.0, response.getPurchasePrice());
        assertEquals(10.5, response.getProfitLossPercentage());
    }

    @Test
    void testBigDecimalFields() {
        StockResponse response = new StockResponse();

        BigDecimal price = new BigDecimal("200.00");
        BigDecimal value = new BigDecimal("1000.00");
        BigDecimal profit = new BigDecimal("150.00");

        response.setCurrentPrice(price);
        response.setCurrentValue(value);
        response.setProfitLoss(profit);

        assertEquals(price, response.getCurrentPrice());
        assertEquals(value, response.getCurrentValue());
        assertEquals(profit, response.getProfitLoss());
    }
}
