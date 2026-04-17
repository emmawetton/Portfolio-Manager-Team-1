package com.portfolio.api;

import com.portfolio.api.dto.CreatePortfolioRequest;
import com.portfolio.api.dto.PortfolioSummaryResponse;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.model.Portfolio;
import com.portfolio.api.repository.PortfolioRepository;
import com.portfolio.api.service.PortfolioService;
import com.portfolio.api.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioApiApplicationTests {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void shouldCreatePortfolioSuccessfully() {
        // Arrange
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("Tech Stocks");
        request.setDescription("My technology investments");

        Portfolio savedPortfolio = new Portfolio();
        savedPortfolio.setId(1L);
        savedPortfolio.setName("Tech Stocks");
        savedPortfolio.setDescription("My technology investments");
        savedPortfolio.setCreatedDate(LocalDate.now());
        savedPortfolio.setStocks(List.of());

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(savedPortfolio);
        when(stockService.getAllStocks(any())).thenReturn(List.of());

        // Act
        PortfolioSummaryResponse result = portfolioService.createPortfolio(request);

        // Assert
        assertThat(result.getName()).isEqualTo("Tech Stocks");
        assertThat(result.getDescription()).isEqualTo("My technology investments");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNumberOfStocks()).isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenPortfolioNotFound() {
        // Arrange
        when(portfolioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.getPortfolioById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Portfolio not found with id: 999");
    }
}