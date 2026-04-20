package com.portfolio.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.portfolio.api.dto.*;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.model.Portfolio;
import com.portfolio.api.repository.PortfolioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class PortfolioServiceTest {

    private PortfolioRepository portfolioRepository;
    private StockService stockService;
    private PortfolioService portfolioService;

    @BeforeEach
    void setup() {
        portfolioRepository = Mockito.mock(PortfolioRepository.class);
        stockService = Mockito.mock(StockService.class);
        portfolioService = new PortfolioService(portfolioRepository, stockService);
    }

    // ---------------------------------------------------------
    // getAllPortfolios
    // ---------------------------------------------------------

    @Test
    void testGetAllPortfolios() {
        Portfolio p = new Portfolio();
        p.setId(1L);
        p.setName("Tech");
        p.setDescription("Tech portfolio");
        p.setCreatedDate(LocalDate.now());

        when(portfolioRepository.findAll()).thenReturn(List.of(p));
        when(stockService.getAllStocks(1L)).thenReturn(List.of());

        List<PortfolioSummaryResponse> result = portfolioService.getAllPortfolios();

        assertEquals(1, result.size());
        assertEquals("Tech", result.get(0).getName());
    }

    // ---------------------------------------------------------
    // getPortfolioById
    // ---------------------------------------------------------

    @Test
    void testGetPortfolioByIdSuccess() {
        Portfolio p = new Portfolio();
        p.setId(1L);
        p.setName("Growth");
        p.setDescription("Growth portfolio");
        p.setCreatedDate(LocalDate.now());

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(p));
        when(stockService.getAllStocks(1L)).thenReturn(List.of());

        PortfolioSummaryResponse response = portfolioService.getPortfolioById(1L);

        assertEquals("Growth", response.getName());
    }

    @Test
    void testGetPortfolioByIdNotFound() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> portfolioService.getPortfolioById(1L));
    }

    // ---------------------------------------------------------
    // createPortfolio
    // ---------------------------------------------------------

    @Test
    void testCreatePortfolio() {
        CreatePortfolioRequest req = new CreatePortfolioRequest();
        req.setName("New Portfolio");
        req.setDescription("Description");

        Portfolio saved = new Portfolio();
        saved.setId(10L);
        saved.setName("New Portfolio");
        saved.setDescription("Description");
        saved.setCreatedDate(LocalDate.now());

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(saved);
        when(stockService.getAllStocks(10L)).thenReturn(List.of());

        PortfolioSummaryResponse response = portfolioService.createPortfolio(req);

        assertEquals(10L, response.getId());
        assertEquals("New Portfolio", response.getName());
    }

    // ---------------------------------------------------------
    // updatePortfolio
    // ---------------------------------------------------------

    @Test
    void testUpdatePortfolioSuccess() {
        Portfolio existing = new Portfolio();
        existing.setId(5L);
        existing.setName("Old");
        existing.setDescription("Old desc");

        UpdatePortfolioRequest req = new UpdatePortfolioRequest();
        req.setName("Updated");
        req.setDescription("Updated desc");

        when(portfolioRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(portfolioRepository.save(existing)).thenReturn(existing);
        when(stockService.getAllStocks(5L)).thenReturn(List.of());

        PortfolioSummaryResponse response = portfolioService.updatePortfolio(5L, req);

        assertEquals("Updated", response.getName());
        assertEquals("Updated desc", response.getDescription());
    }

    @Test
    void testUpdatePortfolioNotFound() {
        when(portfolioRepository.findById(5L)).thenReturn(Optional.empty());

        UpdatePortfolioRequest req = new UpdatePortfolioRequest();
        req.setName("X");
        req.setDescription("Y");

        assertThrows(ResourceNotFoundException.class,
                () -> portfolioService.updatePortfolio(5L, req));
    }

    // ---------------------------------------------------------
    // deletePortfolio
    // ---------------------------------------------------------

    @Test
    void testDeletePortfolioSuccess() {
        Portfolio p = new Portfolio();
        p.setId(3L);

        when(portfolioRepository.findById(3L)).thenReturn(Optional.of(p));

        portfolioService.deletePortfolio(3L);

        verify(portfolioRepository, times(1)).deleteById(3L);
    }

    @Test
    void testDeletePortfolioNotFound() {
        when(portfolioRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> portfolioService.deletePortfolio(3L));
    }

    // ---------------------------------------------------------
    // mapToSummaryResponse (indirectly tested via public methods)
    // ---------------------------------------------------------

    @Test
    void testPortfolioCalculations() {
        Portfolio p = new Portfolio();
        p.setId(1L);
        p.setName("Calc");
        p.setDescription("Test");
        p.setCreatedDate(LocalDate.now());

        StockResponse s1 = new StockResponse();
        s1.setPurchasePrice(100.0);
        s1.setQuantity(2.0);
        s1.setCurrentValue(new BigDecimal("250.00")); // profit 50

        StockResponse s2 = new StockResponse();
        s2.setPurchasePrice(50.0);
        s2.setQuantity(1.0);
        s2.setCurrentValue(new BigDecimal("40.00")); // loss -10

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(p));
        when(stockService.getAllStocks(1L)).thenReturn(List.of(s1, s2));

        PortfolioSummaryResponse response = portfolioService.getPortfolioById(1L);

        assertEquals(new BigDecimal("290.00"), response.getTotalValue());
        assertEquals(new BigDecimal("40.00"), response.getTotalProfitLoss()); // 50 - 10
        assertEquals(16.0, response.getTotalProfitLossPercentage()); // 40 / 250 = 0.16 → 16%
    }
}

