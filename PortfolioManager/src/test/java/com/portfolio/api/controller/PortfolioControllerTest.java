package com.portfolio.api.controller;

import com.portfolio.api.dto.*;
import com.portfolio.api.service.PortfolioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PortfolioControllerTest {

    private PortfolioService portfolioService;
    private PortfolioController controller;

    @BeforeEach
    void setup() {
        portfolioService = mock(PortfolioService.class);
        controller = new PortfolioController(portfolioService);
    }

    // ---------------------------------------------------------
    // GET /api/portfolios
    // ---------------------------------------------------------

    @Test
    void testGetAllPortfolios() {
        PortfolioSummaryResponse p = new PortfolioSummaryResponse();
        p.setId(1L);
        p.setName("Tech");
        p.setDescription("Tech portfolio");
        p.setCreatedDate(LocalDate.now());

        when(portfolioService.getAllPortfolios()).thenReturn(List.of(p));

        ResponseEntity<List<PortfolioSummaryResponse>> response = controller.getAllPortfolios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Tech", response.getBody().get(0).getName());
    }

    // ---------------------------------------------------------
    // GET /api/portfolios/{id}
    // ---------------------------------------------------------

    @Test
    void testGetPortfolioById() {
        PortfolioSummaryResponse p = new PortfolioSummaryResponse();
        p.setId(1L);
        p.setName("Growth");

        when(portfolioService.getPortfolioById(1L)).thenReturn(p);

        ResponseEntity<PortfolioSummaryResponse> response = controller.getPortfolioById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Growth", response.getBody().getName());
    }

    // ---------------------------------------------------------
    // POST /api/portfolios
    // ---------------------------------------------------------

    @Test
    void testCreatePortfolio() {
        CreatePortfolioRequest req = new CreatePortfolioRequest();
        req.setName("New Portfolio");
        req.setDescription("Description");

        PortfolioSummaryResponse saved = new PortfolioSummaryResponse();
        saved.setId(10L);
        saved.setName("New Portfolio");

        when(portfolioService.createPortfolio(req)).thenReturn(saved);

        ResponseEntity<PortfolioSummaryResponse> response = controller.createPortfolio(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    // ---------------------------------------------------------
    // PUT /api/portfolios/{id}
    // ---------------------------------------------------------

    @Test
    void testUpdatePortfolio() {
        UpdatePortfolioRequest req = new UpdatePortfolioRequest();
        req.setName("Updated");
        req.setDescription("Updated desc");

        PortfolioSummaryResponse updated = new PortfolioSummaryResponse();
        updated.setId(5L);
        updated.setName("Updated");

        when(portfolioService.updatePortfolio(5L, req)).thenReturn(updated);

        ResponseEntity<PortfolioSummaryResponse> response = controller.updatePortfolio(5L, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", response.getBody().getName());
    }

    // ---------------------------------------------------------
    // DELETE /api/portfolios/{id}
    // ---------------------------------------------------------

    @Test
    void testDeletePortfolio() {
        doNothing().when(portfolioService).deletePortfolio(3L);

        ResponseEntity<Void> response = controller.deletePortfolio(3L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(portfolioService, times(1)).deletePortfolio(3L);
    }
}
