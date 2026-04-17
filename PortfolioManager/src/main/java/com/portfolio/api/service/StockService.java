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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final YahooFinanceService yahooFinanceService;

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

        String symbol = request.getSymbol().toUpperCase();

        // Check if stock already exists in this portfolio
        List<Stock> existingStocks = stockRepository.findByPortfolioId(portfolioId);
        Stock stock = existingStocks.stream()
                .filter(s -> s.getShortTicketCode().equals(symbol))
                .findFirst()
                .orElse(null);

        if (stock != null) {
            // Stock already exists — just update the quantity
            stock.setQuantity(stock.getQuantity() + request.getQuantity());
            Stock saved = stockRepository.save(stock);
            return mapToStockResponse(saved);
        }

        // Stock does not exist — create a new one
        String companyName = yahooFinanceService.getStockName(symbol);

        stock = new Stock();
        stock.setName(companyName);
        stock.setShortTicketCode(symbol);
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

    public StockTrendResponse getStockTrends(Long portfolioId, Long stockId, int months) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + stockId));

        if (!stock.getPortfolio().getId().equals(portfolioId)) {
            throw new ResourceNotFoundException("Stock does not belong to portfolio with id: " + portfolioId);
        }

        List<YahooFinanceService.MonthlyPrice> historicalPrices = yahooFinanceService
                .getHistoricalPrices(stock.getShortTicketCode(), months);

        List<StockTrendResponse.TrendPoint> trendPoints = historicalPrices.stream()
                .map(p -> new StockTrendResponse.TrendPoint(p.getDate(), p.getPrice()))
                .collect(Collectors.toList());

        StockTrendResponse response = new StockTrendResponse();
        response.setSymbol(stock.getShortTicketCode());
        response.setName(stock.getName());
        response.setTrends(trendPoints);

        return response;
    }

    private StockResponse mapToStockResponse(Stock stock) {
        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setName(stock.getName());
        response.setSymbol(stock.getShortTicketCode());
        response.setQuantity(stock.getQuantity());
        response.setPurchasePrice(stock.getPurchasePrice());
        response.setPurchaseDate(stock.getPurchaseDate());

        try {
            BigDecimal currentPrice;

            // Check if we already fetched the price today
            if (stock.getLastKnownPrice() != null &&
                    stock.getLastPriceUpdated() != null &&
                    stock.getLastPriceUpdated().isEqual(LocalDate.now())) {

                // Use cached price — no API call needed
                currentPrice = stock.getLastKnownPrice();

            } else {

                // Fetch fresh price from Alpha Vantage
                currentPrice = yahooFinanceService.getCurrentPrice(stock.getShortTicketCode());

                // Save the price and today's date to the database
                stock.setLastKnownPrice(currentPrice);
                stock.setLastPriceUpdated(LocalDate.now());
                stockRepository.save(stock);
            }

            BigDecimal quantity = BigDecimal.valueOf(stock.getQuantity());
            BigDecimal purchasePrice = BigDecimal.valueOf(stock.getPurchasePrice());

            BigDecimal currentValue = currentPrice.multiply(quantity);
            BigDecimal totalPurchaseCost = purchasePrice.multiply(quantity);
            BigDecimal profitLoss = currentValue.subtract(totalPurchaseCost);
            Double profitLossPercentage = profitLoss
                    .divide(totalPurchaseCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();

            response.setCurrentPrice(currentPrice.setScale(2, RoundingMode.HALF_UP));
            response.setCurrentValue(currentValue.setScale(2, RoundingMode.HALF_UP));
            response.setProfitLoss(profitLoss.setScale(2, RoundingMode.HALF_UP));
            response.setProfitLossPercentage(Math.round(profitLossPercentage * 100.0) / 100.0);

        } catch (Exception e) {
            // If everything fails use last known price if available
            BigDecimal fallbackPrice = stock.getLastKnownPrice() != null
                    ? stock.getLastKnownPrice()
                    : BigDecimal.ZERO;

            BigDecimal quantity = BigDecimal.valueOf(stock.getQuantity());
            BigDecimal purchasePrice = BigDecimal.valueOf(stock.getPurchasePrice());
            BigDecimal currentValue = fallbackPrice.multiply(quantity);
            BigDecimal totalPurchaseCost = purchasePrice.multiply(quantity);
            BigDecimal profitLoss = currentValue.subtract(totalPurchaseCost);

            response.setCurrentPrice(fallbackPrice.setScale(2, RoundingMode.HALF_UP));
            response.setCurrentValue(currentValue.setScale(2, RoundingMode.HALF_UP));
            response.setProfitLoss(profitLoss.setScale(2, RoundingMode.HALF_UP));
            response.setProfitLossPercentage(0.0);
        }

        return response;
    }
}