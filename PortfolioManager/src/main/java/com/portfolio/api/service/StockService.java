package com.portfolio.api.service;

import java.util.List;
import com.portfolio.api.model.Stocks;
import org.springframework.stereotype.Service;
import com.portfolio.api.respository.StockRepository;

@Service
public class StockService {
    private StockRepository stockRepository;
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stocks> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stocks addStock(Stocks stock) {
        return stockRepository.save(stock);
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    public double calculatePerformance(Stocks stock) {
        return stock.getQuantity() * (stock.getCurrentPrice() - stock.getPurchasePrice());
    }

  
}
