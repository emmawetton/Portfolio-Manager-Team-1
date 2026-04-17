package com.portfolio.api.service;

import com.portfolio.api.dto.*;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.model.Portfolio;
import com.portfolio.api.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

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
        response.setNumberOfStocks(portfolio.getStocks() == null ? 0 : portfolio.getStocks().size());
        response.setTotalValue(BigDecimal.ZERO);
        response.setTotalProfitLoss(BigDecimal.ZERO);
        response.setTotalProfitLossPercentage(0.0);
        response.setStocks(List.of());
        return response;
    }
}