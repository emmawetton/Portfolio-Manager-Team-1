package com.portfolio.api.controller;

import com.portfolio.api.dto.*;
import com.portfolio.api.service.StockService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StockControllerTest {

    private StockService stockService;
    private StockController controller;

    @BeforeEach
    void setup() {
        stockService = mock(StockService.class);
        controller = new StockController(stockService);
    }

    // ---------------------------------------------------------
    // GET /api/portfolios/{portfolioId}/stocks
    // ---------------------------------------------------------

    @Test
    void testGetAllStocks() {
        StockResponse s = new StockResponse();
        s.setId(1L);
        s.setName("Apple");
        s.setSymbol("AAPL");
        s.setQuantity(2.0);
        s.setPurchasePrice(100.0);
        s.setCurrentPrice(new BigDecimal("150.00"));
        s.setPurchaseDate(LocalDate.now());

        when(stockService.getAllStocks(1L)).thenReturn(List.of(s));

        ResponseEntity<List<StockResponse>> response = controller.getAllStocks(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Apple", response.getBody().get(0).getName());
    }

    // ---------------------------------------------------------
    // GET /api/portfolios/{portfolioId}/stocks/{stockId}
    // ---------------------------------------------------------

    @Test
    void testGetStockById() {
        StockResponse s = new StockResponse();
        s.setId(10L);
        s.setName("Microsoft");
        s.setSymbol("MSFT");

        when(stockService.getStockById(1L, 10L)).thenReturn(s);

        ResponseEntity<StockResponse> response = controller.getStockById(1L, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Microsoft", response.getBody().getName());
    }

    // ---------------------------------------------------------
    // GET /api/portfolios/{portfolioId}/stocks/{stockId}/trends
    // ---------------------------------------------------------

    @Test
    void testGetStockTrends() {
        StockTrendResponse trend = new StockTrendResponse();
        trend.setSymbol("AAPL");
        trend.setName("Apple");
        trend.setTrends(List.of(
                new StockTrendResponse.TrendPoint("2024-01-01", new BigDecimal("150.00")),
                new StockTrendResponse.TrendPoint("2023-12-01", new BigDecimal("145.00"))
        ));

        when(stockService.getStockTrends(1L, 10L, 6)).thenReturn(trend);

        ResponseEntity<StockTrendResponse> response = controller.getStockTrends(1L, 10L, 6);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("AAPL", response.getBody().getSymbol());
        assertEquals(2, response.getBody().getTrends().size());
    }

    // ---------------------------------------------------------
    // POST /api/portfolios/{portfolioId}/stocks
    // ---------------------------------------------------------

    @Test
    void testAddStock() {
        CreateStockRequest req = new CreateStockRequest();
        req.setSymbol("AAPL");
        req.setQuantity(2.0);
        req.setPurchasePrice(100.0);
        req.setPurchaseDate(LocalDate.now());

        StockResponse saved = new StockResponse();
        saved.setId(5L);
        saved.setSymbol("AAPL");
        saved.setName("Apple Inc.");

        when(stockService.addStock(1L, req)).thenReturn(saved);

        ResponseEntity<StockResponse> response = controller.addStock(1L, req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, response.getBody().getId());
    }

    // ---------------------------------------------------------
    // PUT /api/portfolios/{portfolioId}/stocks/{stockId}
    // ---------------------------------------------------------

    @Test
    void testUpdateStock() {
        UpdateStockRequest req = new UpdateStockRequest();
        req.setQuantity(5.0);
        req.setPurchasePrice(120.0);

        StockResponse updated = new StockResponse();
        updated.setId(10L);
        updated.setQuantity(5.0);
        updated.setPurchasePrice(120.0);

        when(stockService.updateStock(1L, 10L, req)).thenReturn(updated);

        ResponseEntity<StockResponse> response = controller.updateStock(1L, 10L, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5.0, response.getBody().getQuantity());
    }

    // ---------------------------------------------------------
    // DELETE /api/portfolios/{portfolioId}/stocks/{stockId}
    // ---------------------------------------------------------

    @Test
    void testDeleteStock() {
        doNothing().when(stockService).deleteStock(1L, 10L);

        ResponseEntity<Void> response = controller.deleteStock(1L, 10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stockService, times(1)).deleteStock(1L, 10L);
    }
}
