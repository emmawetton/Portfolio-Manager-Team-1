package com.portfolio.api.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PortfolioTest {

    @Test
    void testPortfolioFieldsAndGettersSetters() {
        Portfolio portfolio = new Portfolio();

        Long id = 1L;
        String name = "Tech Investments";
        String description = "Portfolio focused on technology stocks";
        LocalDate createdDate = LocalDate.of(2024, 1, 5);

        portfolio.setId(id);
        portfolio.setName(name);
        portfolio.setDescription(description);
        portfolio.setCreatedDate(createdDate);

        assertEquals(id, portfolio.getId());
        assertEquals(name, portfolio.getName());
        assertEquals(description, portfolio.getDescription());
        assertEquals(createdDate, portfolio.getCreatedDate());
    }

    @Test
    void testPortfolioStockRelationship() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        Stock stock1 = new Stock();
        stock1.setId(10L);
        stock1.setPortfolio(portfolio);

        Stock stock2 = new Stock();
        stock2.setId(11L);
        stock2.setPortfolio(portfolio);

        List<Stock> stocks = new ArrayList<>();
        stocks.add(stock1);
        stocks.add(stock2);

        portfolio.setStocks(stocks);

        assertEquals(2, portfolio.getStocks().size());
        assertTrue(portfolio.getStocks().contains(stock1));
        assertTrue(portfolio.getStocks().contains(stock2));

        // Ensure the relationship is bidirectional
        assertEquals(portfolio, stock1.getPortfolio());
        assertEquals(portfolio, stock2.getPortfolio());
    }

    @Test
    void testEqualsAndHashCode() {
        Portfolio p1 = new Portfolio();
        p1.setId(1L);

        Portfolio p2 = new Portfolio();
        p2.setId(1L);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Sample Portfolio");

        assertNotNull(portfolio.toString());
        assertTrue(portfolio.toString().contains("Sample Portfolio"));
    }
}
