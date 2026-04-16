package com.portfolio.api.controller;

import com.portfolio.api.dto.*;
import com.portfolio.api.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(stockService.getAllStocks(portfolioId));
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long portfolioId, @PathVariable Long stockId) {
        return ResponseEntity.ok(stockService.getStockById(portfolioId, stockId));
    }

    @PostMapping
    public ResponseEntity<StockResponse> addStock(@PathVariable Long portfolioId, @Valid @RequestBody CreateStockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.addStock(portfolioId, request));
    }

    @PutMapping("/{stockId}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable Long portfolioId, @PathVariable Long stockId, @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(stockService.updateStock(portfolioId, stockId, request));
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long portfolioId, @PathVariable Long stockId) {
        stockService.deleteStock(portfolioId, stockId);
        return ResponseEntity.noContent().build();
    }
}