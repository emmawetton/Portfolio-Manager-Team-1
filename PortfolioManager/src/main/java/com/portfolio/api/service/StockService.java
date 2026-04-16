package com.portfolio.api.service;

import com.portfolio.api.dto.*;
import com.portfolio.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.portfolio.api.model.Portfolio;
import com.portfolio.api.model.Stock;
import com.portfolio.api.repository.PortfolioRepository;
import com.portfolio.api.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;

    public List<StockResponse> getAllStocks(Long portfolioId) {
        return stockRepository.findByPortfolioId(portfolioId)
                .stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    public StockResponse getStockById(Long portfolioId, Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + stockId));
        if (!stock.getPortfolio().getId().equals(portfolioId)) {
            throw new ResourceNotFoundException("Stock does not belong to portfolio with id: " + portfolioId);
        }
        return mapToStockResponse(stock);
    }

    public StockResponse addStock(Long portfolioId, CreateStockRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));
        Stock stock = new Stock();
        stock.setName(request.getSymbol());
        stock.setShortTicketCode(request.getSymbol().toUpperCase());
        stock.setQuantity(request.getQuantity());
        stock.setPurchasePrice(request.getPurchasePrice());
        stock.setPurchaseDate(request.getPurchaseDate());
        stock.setPortfolio(portfolio);
        Stock saved = stockRepository.save(stock);
        return mapToStockResponse(saved);
    }

    public StockResponse updateStock(Long portfolioId, Long stockId, UpdateStockRequest request) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + stockId));
        if (!stock.getPortfolio().getId().equals(portfolioId)) {
            throw new ResourceNotFoundException("Stock does not belong to portfolio with id: " + portfolioId);
        }
        stock.setQuantity(request.getQuantity());
        stock.setPurchasePrice(request.getPurchasePrice());
        Stock saved = stockRepository.save(stock);
        return mapToStockResponse(saved);
    }

    public void deleteStock(Long portfolioId, Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + stockId));
        if (!stock.getPortfolio().getId().equals(portfolioId)) {
            throw new ResourceNotFoundException("Stock does not belong to portfolio with id: " + portfolioId);
        }
        stockRepository.deleteById(stockId);
    }

    private StockResponse mapToStockResponse(Stock stock) {
        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setName(stock.getName());
        response.setSymbol(stock.getShortTicketCode());
        response.setQuantity(stock.getQuantity());
        response.setPurchasePrice(stock.getPurchasePrice());
        response.setPurchaseDate(stock.getPurchaseDate());
        response.setCurrentPrice(BigDecimal.ZERO);
        response.setCurrentValue(BigDecimal.ZERO);
        response.setProfitLoss(BigDecimal.ZERO);
        response.setProfitLossPercentage(0.0);
        return response;
    }
}