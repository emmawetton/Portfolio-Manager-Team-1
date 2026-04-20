package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PortfolioSummaryResponseTest {

    @Test
    void testFieldsAndGettersSetters() {
        PortfolioSummaryResponse response = new PortfolioSummaryResponse();

        Long id = 1L;
        String name = "Tech Portfolio";
        String description = "A portfolio focused on tech stocks";
        LocalDate createdDate = LocalDate.of(2024, 1, 1);
        BigDecimal totalValue = new BigDecimal("50000.00");
        BigDecimal totalProfitLoss = new BigDecimal("2500.00");
        Double totalProfitLossPercentage = 5.0;
        int numberOfStocks = 3;

        List<StockResponse> stocks = new ArrayList<>();
        StockResponse stock1 = new StockResponse();
        stock1.setSymbol("AAPL");
        stocks.add(stock1);

        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setCreatedDate(createdDate);
        response.setTotalValue(totalValue);
        response.setTotalProfitLoss(totalProfitLoss);
        response.setTotalProfitLossPercentage(totalProfitLossPercentage);
        response.setNumberOfStocks(numberOfStocks);
        response.setStocks(stocks);

        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(createdDate, response.getCreatedDate());
        assertEquals(totalValue, response.getTotalValue());
        assertEquals(totalProfitLoss, response.getTotalProfitLoss());
        assertEquals(totalProfitLossPercentage, response.getTotalProfitLossPercentage());
        assertEquals(numberOfStocks, response.getNumberOfStocks());
        assertEquals(stocks, response.getStocks());
        assertEquals("AAPL", response.getStocks().get(0).getSymbol());
    }

    @Test
    void testEqualsAndHashCode() {
        PortfolioSummaryResponse r1 = new PortfolioSummaryResponse();
        r1.setId(1L);
        r1.setName("Portfolio A");

        PortfolioSummaryResponse r2 = new PortfolioSummaryResponse();
        r2.setId(1L);
        r2.setName("Portfolio A");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        PortfolioSummaryResponse response = new PortfolioSummaryResponse();
        response.setName("Sample Portfolio");

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("Sample Portfolio"));
    }

    @Test
    void testStocksListHandling() {
        PortfolioSummaryResponse response = new PortfolioSummaryResponse();

        StockResponse stock = new StockResponse();
        stock.setSymbol("GOOGL");

        List<StockResponse> stocks = new ArrayList<>();
        stocks.add(stock);

        response.setStocks(stocks);

        assertEquals(1, response.getStocks().size());
        assertEquals("GOOGL", response.getStocks().get(0).getSymbol());
    }
}

