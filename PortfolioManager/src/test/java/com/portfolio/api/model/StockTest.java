package com.portfolio.api.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class StockTest {

    @Test
    void testStockFieldsAndGettersSetters() {
        Stock stock = new Stock();

        Long id = 1L;
        String name = "Apple Inc.";
        double purchasePrice = 150.50;
        double quantity = 10;
        LocalDate purchaseDate = LocalDate.of(2024, 1, 10);
        String shortTicketCode = "AAPL";
        BigDecimal lastKnownPrice = new BigDecimal("155.75");
        LocalDate lastPriceUpdated = LocalDate.of(2024, 1, 15);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(100L);

        stock.setId(id);
        stock.setName(name);
        stock.setPurchasePrice(purchasePrice);
        stock.setQuantity(quantity);
        stock.setPurchaseDate(purchaseDate);
        stock.setShortTicketCode(shortTicketCode);
        stock.setPortfolio(portfolio);
        stock.setLastKnownPrice(lastKnownPrice);
        stock.setLastPriceUpdated(lastPriceUpdated);

        assertEquals(id, stock.getId());
        assertEquals(name, stock.getName());
        assertEquals(purchasePrice, stock.getPurchasePrice());
        assertEquals(quantity, stock.getQuantity());
        assertEquals(purchaseDate, stock.getPurchaseDate());
        assertEquals(shortTicketCode, stock.getShortTicketCode());
        assertEquals(portfolio, stock.getPortfolio());
        assertEquals(lastKnownPrice, stock.getLastKnownPrice());
        assertEquals(lastPriceUpdated, stock.getLastPriceUpdated());
    }

    @Test
    void testEqualsAndHashCode() {
        Stock stock1 = new Stock();
        stock1.setId(1L);

        Stock stock2 = new Stock();
        stock2.setId(1L);

        assertEquals(stock1, stock2);
        assertEquals(stock1.hashCode(), stock2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setName("Test Stock");

        assertNotNull(stock.toString());
        assertTrue(stock.toString().contains("Test Stock"));
    }
}
