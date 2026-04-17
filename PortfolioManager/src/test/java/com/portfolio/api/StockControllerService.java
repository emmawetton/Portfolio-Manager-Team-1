package com.portfolio.api;

import com.portfolio.api.controller.StockController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.api.dto.CreateStockRequest;
import com.portfolio.api.dto.StockResponse;
import com.portfolio.api.dto.StockTrendResponse;
import com.portfolio.api.dto.UpdateStockRequest;
import com.portfolio.api.exception.GlobalExceptionHandler;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.service.StockService;
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
class StockControllerTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockController stockController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StockResponse stockResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(stockController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        stockResponse = new StockResponse();
        stockResponse.setId(1L);
        stockResponse.setName("Apple Inc");
        stockResponse.setSymbol("AAPL");
        stockResponse.setQuantity(10.0);
        stockResponse.setPurchasePrice(189.50);
        stockResponse.setPurchaseDate(LocalDate.of(2024, 1, 15));
        stockResponse.setCurrentPrice(new BigDecimal("263.40"));
        stockResponse.setCurrentValue(new BigDecimal("2634.00"));
        stockResponse.setProfitLoss(new BigDecimal("739.00"));
        stockResponse.setProfitLossPercentage(39.0);
    }

    @Test
    void shouldGetAllStocks() throws Exception {
        // Arrange
        StockResponse stock2 = new StockResponse();
        stock2.setId(2L);
        stock2.setName("Tesla Inc");
        stock2.setSymbol("TSLA");
        stock2.setQuantity(5.0);
        stock2.setPurchasePrice(220.0);
        stock2.setPurchaseDate(LocalDate.of(2024, 1, 20));
        stock2.setCurrentPrice(new BigDecimal("388.79"));
        stock2.setCurrentValue(new BigDecimal("1943.95"));
        stock2.setProfitLoss(new BigDecimal("843.95"));
        stock2.setProfitLossPercentage(76.72);

        when(stockService.getAllStocks(1L))
                .thenReturn(Arrays.asList(stockResponse, stock2));

        // Act & Assert
        mockMvc.perform(get("/api/portfolios/1/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[1].symbol").value("TSLA"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetStockById() throws Exception {
        // Arrange
        when(stockService.getStockById(1L, 1L)).thenReturn(stockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/portfolios/1/stocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc"));
    }

    @Test
    void shouldReturn404WhenStockNotFound() throws Exception {
        // Arrange
        when(stockService.getStockById(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Stock not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/portfolios/1/stocks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Stock not found with id: 999"));
    }

    @Test
    void shouldAddStock() throws Exception {
        // Arrange
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("AAPL");
        request.setQuantity(10.0);
        request.setPurchasePrice(189.50);
        request.setPurchaseDate(LocalDate.of(2024, 1, 15));

        when(stockService.addStock(eq(1L), any(CreateStockRequest.class)))
                .thenReturn(stockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/portfolios/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc"));
    }

    @Test
    void shouldReturn400WhenAddingStockWithEmptySymbol() throws Exception {
        // Arrange
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("");
        request.setQuantity(10.0);
        request.setPurchasePrice(189.50);
        request.setPurchaseDate(LocalDate.of(2024, 1, 15));

        // Act & Assert
        mockMvc.perform(post("/api/portfolios/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAddingStockWithNegativeQuantity() throws Exception {
        // Arrange
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("AAPL");
        request.setQuantity(-5.0);
        request.setPurchasePrice(189.50);
        request.setPurchaseDate(LocalDate.of(2024, 1, 15));

        // Act & Assert
        mockMvc.perform(post("/api/portfolios/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateStock() throws Exception {
        // Arrange
        UpdateStockRequest request = new UpdateStockRequest();
        request.setQuantity(20.0);
        request.setPurchasePrice(189.50);

        StockResponse updatedResponse = new StockResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Apple Inc");
        updatedResponse.setSymbol("AAPL");
        updatedResponse.setQuantity(20.0);
        updatedResponse.setPurchasePrice(189.50);
        updatedResponse.setPurchaseDate(LocalDate.of(2024, 1, 15));
        updatedResponse.setCurrentPrice(new BigDecimal("263.40"));
        updatedResponse.setCurrentValue(new BigDecimal("5268.00"));
        updatedResponse.setProfitLoss(new BigDecimal("1478.00"));
        updatedResponse.setProfitLossPercentage(38.98);

        when(stockService.updateStock(eq(1L), eq(1L), any(UpdateStockRequest.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/portfolios/1/stocks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(20.0));
    }

    @Test
    void shouldDeleteStock() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/portfolios/1/stocks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentStock() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Stock not found with id: 999"))
                .when(stockService).deleteStock(1L, 999L);

        // Act & Assert
        mockMvc.perform(delete("/api/portfolios/1/stocks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Stock not found with id: 999"));
    }

    @Test
    void shouldGetStockTrends() throws Exception {
        // Arrange
        StockTrendResponse trendResponse = new StockTrendResponse();
        trendResponse.setSymbol("AAPL");
        trendResponse.setName("Apple Inc");
        trendResponse.setTrends(Arrays.asList(
                new StockTrendResponse.TrendPoint("2024-01-01", new BigDecimal("189.50")),
                new StockTrendResponse.TrendPoint("2024-02-01", new BigDecimal("195.00")),
                new StockTrendResponse.TrendPoint("2024-03-01", new BigDecimal("202.00"))));

        when(stockService.getStockTrends(1L, 1L, 6)).thenReturn(trendResponse);

        // Act & Assert
        mockMvc.perform(get("/api/portfolios/1/stocks/1/trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc"))
                .andExpect(jsonPath("$.trends.length()").value(3))
                .andExpect(jsonPath("$.trends[0].date").value("2024-01-01"));
    }
}