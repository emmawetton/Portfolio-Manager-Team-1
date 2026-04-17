package com.portfolio.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.api.model.Portfolio;
import com.portfolio.api.model.Stock;

@SpringBootTest
@Transactional
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void testSaveAndFindById() {
        Portfolio portfolio = new Portfolio();
        portfolio.setName("Tech Portfolio");
        portfolio.setCreatedDate(LocalDate.now());
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        Stock stock = new Stock();
        stock.setName("Apple Inc.");
        stock.setShortTicketCode("AAPL");
        stock.setPurchasePrice(150.0);
        stock.setQuantity(10.0);
        stock.setPurchaseDate(LocalDate.of(2024, 1, 1));
        stock.setLastKnownPrice(new BigDecimal("175.00"));
        stock.setLastPriceUpdated(LocalDate.of(2024, 1, 5));
        stock.setPortfolio(savedPortfolio);

        Stock saved = stockRepository.save(stock);

        assertNotNull(saved.getId());

        Stock found = stockRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Apple Inc.", found.getName());
        assertEquals("AAPL", found.getShortTicketCode());
        assertEquals(150.0, found.getPurchasePrice());
        assertEquals(10.0, found.getQuantity());
        assertEquals(savedPortfolio.getId(), found.getPortfolio().getId());
    }

    @Test
    void testFindByPortfolioId() {
        Portfolio portfolio = new Portfolio();
        portfolio.setName("Growth Portfolio");
        portfolio.setCreatedDate(LocalDate.now());
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        Stock stock1 = new Stock();
        stock1.setName("Google");
        stock1.setShortTicketCode("GOOGL");
        stock1.setPurchasePrice(120.0);
        stock1.setQuantity(5.0);
        stock1.setPurchaseDate(LocalDate.now());
        stock1.setPortfolio(savedPortfolio);

        Stock stock2 = new Stock();
        stock2.setName("Microsoft");
        stock2.setShortTicketCode("MSFT");
        stock2.setPurchasePrice(300.0);
        stock2.setQuantity(3.0);
        stock2.setPurchaseDate(LocalDate.now());
        stock2.setPortfolio(savedPortfolio);

        stockRepository.save(stock1);
        stockRepository.save(stock2);

        List<Stock> stocks = stockRepository.findByPortfolioId(savedPortfolio.getId());

        assertEquals(2, stocks.size());
        assertTrue(stocks.stream().anyMatch(s -> s.getShortTicketCode().equals("GOOGL")));
        assertTrue(stocks.stream().anyMatch(s -> s.getShortTicketCode().equals("MSFT")));
    }

    @Test
    void testDelete() {
        Portfolio portfolio = new Portfolio();
        portfolio.setName("Delete Portfolio");
        portfolio.setCreatedDate(LocalDate.now());
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        Stock stock = new Stock();
        stock.setName("Tesla");
        stock.setShortTicketCode("TSLA");
        stock.setPurchasePrice(200.0);
        stock.setQuantity(2.0);
        stock.setPurchaseDate(LocalDate.now());
        stock.setPortfolio(savedPortfolio);

        Stock saved = stockRepository.save(stock);
        Long id = saved.getId();

        stockRepository.delete(saved);

        assertFalse(stockRepository.findById(id).isPresent());
    }
}
