package com.portfolio.api.service;

import com.portfolio.api.dto.*;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.model.Portfolio;
import com.portfolio.api.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;

    public List<PortfolioSummaryResponse> getAllPortfolios() {
        return portfolioRepository.findAll()
                .stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    public PortfolioSummaryResponse getPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        return mapToSummaryResponse(portfolio);
    }

    public PortfolioSummaryResponse createPortfolio(CreatePortfolioRequest request) {
        Portfolio portfolio = new Portfolio();
        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());
        portfolio.setCreatedDate(LocalDate.now());
        Portfolio saved = portfolioRepository.save(portfolio);
        return mapToSummaryResponse(saved);
    }

    public PortfolioSummaryResponse updatePortfolio(Long id, UpdatePortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());
        Portfolio saved = portfolioRepository.save(portfolio);
        return mapToSummaryResponse(saved);
    }

    public void deletePortfolio(Long id) {
        portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        portfolioRepository.deleteById(id);
    }

    private PortfolioSummaryResponse mapToSummaryResponse(Portfolio portfolio) {
        PortfolioSummaryResponse response = new PortfolioSummaryResponse();
        response.setId(portfolio.getId());
        response.setName(portfolio.getName());
        response.setDescription(portfolio.getDescription());
        response.setCreatedDate(portfolio.getCreatedDate());

        // Get all stocks with live prices
        List<StockResponse> stocks = stockService.getAllStocks(portfolio.getId());
        response.setStocks(stocks);
        response.setNumberOfStocks(stocks.size());

        // Calculate portfolio totals
        BigDecimal totalValue = stocks.stream()
                .map(StockResponse::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = stocks.stream()
                .map(s -> BigDecimal.valueOf(s.getPurchasePrice())
                        .multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfitLoss = totalValue.subtract(totalCost);

        Double totalProfitLossPercentage = 0.0;
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            totalProfitLossPercentage = totalProfitLoss
                    .divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            totalProfitLossPercentage = Math.round(totalProfitLossPercentage * 100.0) / 100.0;
        }

        response.setTotalValue(totalValue.setScale(2, RoundingMode.HALF_UP));
        response.setTotalProfitLoss(totalProfitLoss.setScale(2, RoundingMode.HALF_UP));
        response.setTotalProfitLossPercentage(totalProfitLossPercentage);

        return response;
    }
}