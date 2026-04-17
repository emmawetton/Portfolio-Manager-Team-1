package com.portfolio.api.controller;

import com.portfolio.api.controller.PortfolioController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.api.dto.CreatePortfolioRequest;
import com.portfolio.api.dto.PortfolioSummaryResponse;
import com.portfolio.api.dto.UpdatePortfolioRequest;
import com.portfolio.api.exception.GlobalExceptionHandler;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PortfolioSummaryResponse portfolioResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(portfolioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        portfolioResponse = new PortfolioSummaryResponse();
        portfolioResponse.setId(1L);
        portfolioResponse.setName("Tech Stocks");
        portfolioResponse.setDescription("My technology investments");
        portfolioResponse.setCreatedDate(LocalDate.now());
        portfolioResponse.setNumberOfStocks(0);
        portfolioResponse.setTotalValue(BigDecimal.ZERO);
        portfolioResponse.setTotalProfitLoss(BigDecimal.ZERO);
        portfolioResponse.setTotalProfitLossPercentage(0.0);
        portfolioResponse.setStocks(List.of());
    }

    @Test
    void shouldGetAllPortfolios() throws Exception {
        // Arrange
        PortfolioSummaryResponse portfolio2 = new PortfolioSummaryResponse();
        portfolio2.setId(2L);
        portfolio2.setName("Growth Stocks");
        portfolio2.setDescription("High growth investments");
        portfolio2.setCreatedDate(LocalDate.now());
        portfolio2.setNumberOfStocks(0);
        portfolio2.setTotalValue(BigDecimal.ZERO);
        portfolio2.setTotalProfitLoss(BigDecimal.ZERO);
        portfolio2.setTotalProfitLossPercentage(0.0);
        portfolio2.setStocks(List.of());

        when(portfolioService.getAllPortfolios())
                .thenReturn(Arrays.asList(portfolioResponse, portfolio2));

        // Act & Assert
        mockMvc.perform(get("/api/portfolios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tech Stocks"))
                .andExpect(jsonPath("$[1].name").value("Growth Stocks"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetPortfolioById() throws Exception {
        // Arrange
        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolioResponse);

        // Act & Assert
        mockMvc.perform(get("/api/portfolios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tech Stocks"))
                .andExpect(jsonPath("$.description").value("My technology investments"));
    }

    @Test
    void shouldReturn404WhenPortfolioNotFound() throws Exception {
        // Arrange
        when(portfolioService.getPortfolioById(999L))
                .thenThrow(new ResourceNotFoundException("Portfolio not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/portfolios/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Portfolio not found with id: 999"));
    }

    @Test
    void shouldCreatePortfolio() throws Exception {
        // Arrange
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("Tech Stocks");
        request.setDescription("My technology investments");

        when(portfolioService.createPortfolio(any(CreatePortfolioRequest.class)))
                .thenReturn(portfolioResponse);

        // Act & Assert
        mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tech Stocks"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturn400WhenCreatingPortfolioWithEmptyName() throws Exception {
        // Arrange
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("");
        request.setDescription("My technology investments");

        // Act & Assert
        mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePortfolio() throws Exception {
        // Arrange
        UpdatePortfolioRequest request = new UpdatePortfolioRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Description");

        PortfolioSummaryResponse updatedResponse = new PortfolioSummaryResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Name");
        updatedResponse.setDescription("Updated Description");
        updatedResponse.setCreatedDate(LocalDate.now());
        updatedResponse.setNumberOfStocks(0);
        updatedResponse.setTotalValue(BigDecimal.ZERO);
        updatedResponse.setTotalProfitLoss(BigDecimal.ZERO);
        updatedResponse.setTotalProfitLossPercentage(0.0);
        updatedResponse.setStocks(List.of());

        when(portfolioService.updatePortfolio(eq(1L), any(UpdatePortfolioRequest.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/portfolios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void shouldDeletePortfolio() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/portfolios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentPortfolio() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Portfolio not found with id: 999"))
                .when(portfolioService).deletePortfolio(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/portfolios/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Portfolio not found with id: 999"));
    }
}