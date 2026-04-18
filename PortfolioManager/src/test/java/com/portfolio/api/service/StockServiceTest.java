package com.portfolio.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.portfolio.api.dto.*;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.model.Portfolio;
import com.portfolio.api.model.Stock;
import com.portfolio.api.repository.PortfolioRepository;
import com.portfolio.api.repository.StockRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class StockServiceTest {

    private StockRepository stockRepository;
    private PortfolioRepository portfolioRepository;
    private MarketDataService marketDataService;
    private StockService stockService;

    @BeforeEach
    void setup() {
        stockRepository = Mockito.mock(StockRepository.class);
        portfolioRepository = Mockito.mock(PortfolioRepository.class);
        marketDataService = Mockito.mock(MarketDataService.class);

        stockService = new StockService(stockRepository, portfolioRepository, marketDataService);
    }

    // ---------------------------------------------------------
    // getAllStocks
    // ---------------------------------------------------------

    @Test
    void testGetAllStocks() {
        Stock s = new Stock();
        s.setId(1L);
        s.setName("Apple");
        s.setShortTicketCode("AAPL");
        s.setQuantity(2.0);
        s.setPurchasePrice(100.0);
        s.setPurchaseDate(LocalDate.now());

        when(stockRepository.findByPortfolioId(1L)).thenReturn(List.of(s));
        when(marketDataService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("150.00"));
        when(stockRepository.save(any())).thenReturn(s);

        List<StockResponse> result = stockService.getAllStocks(1L);

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getName());
    }

    // ---------------------------------------------------------
    // getStockById
    // ---------------------------------------------------------

    @Test
    void testGetStockByIdSuccess() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);
        s.setShortTicketCode("AAPL");
        s.setQuantity(1.0);
        s.setPurchasePrice(100.0);

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));
        when(marketDataService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("150.00"));
        when(stockRepository.save(any())).thenReturn(s);

        StockResponse response = stockService.getStockById(1L, 10L);

        assertEquals("AAPL", response.getSymbol());
    }

    @Test
    void testGetStockByIdWrongPortfolio() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Portfolio p2 = new Portfolio();
        p2.setId(2L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p2);

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));

        assertThrows(ResourceNotFoundException.class,
                () -> stockService.getStockById(1L, 10L));
    }

    @Test
    void testGetStockByIdNotFound() {
        when(stockRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stockService.getStockById(1L, 10L));
    }

    // ---------------------------------------------------------
    // addStock
    // ---------------------------------------------------------

    @Test
    void testAddStock_NewStock() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        CreateStockRequest req = new CreateStockRequest();
        req.setSymbol("AAPL");
        req.setQuantity(2.0);
        req.setPurchasePrice(100.0);
        req.setPurchaseDate(LocalDate.now());

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(p));
        when(stockRepository.findByPortfolioId(1L)).thenReturn(List.of());
        when(marketDataService.getStockName("AAPL")).thenReturn("Apple Inc.");

        Stock saved = new Stock();
        saved.setId(5L);
        saved.setName("Apple Inc.");
        saved.setShortTicketCode("AAPL");
        saved.setQuantity(2.0);
        saved.setPurchasePrice(100.0);
        saved.setPortfolio(p);

        when(stockRepository.save(any())).thenReturn(saved);
        when(marketDataService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("150.00"));

        StockResponse response = stockService.addStock(1L, req);

        assertEquals("Apple Inc.", response.getName());
        assertEquals("AAPL", response.getSymbol());
    }

    @Test
    void testAddStock_ExistingStockUpdatesQuantity() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock existing = new Stock();
        existing.setId(10L);
        existing.setShortTicketCode("AAPL");
        existing.setQuantity(2.0);
        existing.setPurchasePrice(100.0);
        existing.setPortfolio(p);

        CreateStockRequest req = new CreateStockRequest();
        req.setSymbol("AAPL");
        req.setQuantity(3.0);
        req.setPurchasePrice(100.0);
        req.setPurchaseDate(LocalDate.now());

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(p));
        when(stockRepository.findByPortfolioId(1L)).thenReturn(List.of(existing));
        when(stockRepository.save(existing)).thenReturn(existing);
        when(marketDataService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("150.00"));

        StockResponse response = stockService.addStock(1L, req);

        assertEquals(5.0, response.getQuantity());
    }

    @Test
    void testAddStock_PortfolioNotFound() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());

        CreateStockRequest req = new CreateStockRequest();
        req.setSymbol("AAPL");

        assertThrows(ResourceNotFoundException.class,
                () -> stockService.addStock(1L, req));
    }

    // ---------------------------------------------------------
    // updateStock
    // ---------------------------------------------------------

    @Test
    void testUpdateStockSuccess() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);
        s.setShortTicketCode("AAPL");

        UpdateStockRequest req = new UpdateStockRequest();
        req.setQuantity(5.0);
        req.setPurchasePrice(120.0);

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));
        when(stockRepository.save(s)).thenReturn(s);
        when(marketDataService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("150.00"));

        StockResponse response = stockService.updateStock(1L, 10L, req);

        assertEquals(5.0, response.getQuantity());
        assertEquals(120.0, response.getPurchasePrice());
    }

    @Test
    void testUpdateStockWrongPortfolio() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Portfolio p2 = new Portfolio();
        p2.setId(2L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p2);

        UpdateStockRequest req = new UpdateStockRequest();

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));

        assertThrows(ResourceNotFoundException.class,
                () -> stockService.updateStock(1L, 10L, req));
    }

    // ---------------------------------------------------------
    // deleteStock
    // ---------------------------------------------------------

    @Test
    void testDeleteStockSuccess() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));

        stockService.deleteStock(1L, 10L);

        verify(stockRepository, times(1)).deleteById(10L);
    }

    @Test
    void testDeleteStockWrongPortfolio() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Portfolio p2 = new Portfolio();
        p2.setId(2L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p2);

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));

        assertThrows(ResourceNotFoundException.class,
                () -> stockService.deleteStock(1L, 10L));
    }

    // ---------------------------------------------------------
    // getStockTrends
    // ---------------------------------------------------------

    @Test
    void testGetStockTrends() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);
        s.setShortTicketCode("AAPL");
        s.setName("Apple");

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));

        List<MarketDataService.MonthlyPrice> prices = List.of(
                new MarketDataService.MonthlyPrice("2024-01-01", new BigDecimal("150.00")),
                new MarketDataService.MonthlyPrice("2023-12-01", new BigDecimal("145.00"))
        );

        when(marketDataService.getHistoricalPrices("AAPL", 2)).thenReturn(prices);

        StockTrendResponse response = stockService.getStockTrends(1L, 10L, 2);

        assertEquals("AAPL", response.getSymbol());
        assertEquals(2, response.getTrends().size());
    }

    // ---------------------------------------------------------
    // mapToStockResponse (cached price, API fetch, fallback)
    // ---------------------------------------------------------

    @Test
    void testMapToStockResponse_UsesCachedPrice() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);
        s.setShortTicketCode("AAPL");
        s.setQuantity(2.0);
        s.setPurchasePrice(100.0);
        s.setLastKnownPrice(new BigDecimal("200.00"));
        s.setLastPriceUpdated(LocalDate.now());

        // ⭐ FIX: mock repository lookup
        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));

        // Should NOT call API
        when(marketDataService.getCurrentPrice(anyString()))
                .thenThrow(new AssertionError("Should not call API when cached price exists"));

        StockResponse response = stockService.getStockById(1L, 10L);

        assertEquals(new BigDecimal("200.00"), response.getCurrentPrice());
    }

    @Test
    void testMapToStockResponse_FetchesNewPrice() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);
        s.setShortTicketCode("AAPL");
        s.setQuantity(2.0);
        s.setPurchasePrice(100.0);

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));
        when(marketDataService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("150.00"));
        when(stockRepository.save(any())).thenReturn(s);

        StockResponse response = stockService.getStockById(1L, 10L);

        assertEquals(new BigDecimal("150.00"), response.getCurrentPrice());
    }

    @Test
    void testMapToStockResponse_FallbackLogic() {
        Portfolio p = new Portfolio();
        p.setId(1L);

        Stock s = new Stock();
        s.setId(10L);
        s.setPortfolio(p);
        s.setShortTicketCode("AAPL");
        s.setQuantity(2.0);
        s.setPurchasePrice(100.0);
        s.setLastKnownPrice(new BigDecimal("120.00"));

        when(stockRepository.findById(10L)).thenReturn(Optional.of(s));
        when(marketDataService.getCurrentPrice("AAPL"))
                .thenThrow(new RuntimeException("API down"));

        StockResponse response = stockService.getStockById(1L, 10L);

        assertEquals(new BigDecimal("120.00"), response.getCurrentPrice());
        assertEquals(0.0, response.getProfitLossPercentage());
    }
}
