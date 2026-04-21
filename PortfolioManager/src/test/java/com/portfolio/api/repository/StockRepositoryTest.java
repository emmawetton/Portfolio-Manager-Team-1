// package com.portfolio.api.repository;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

// import com.portfolio.api.model.Portfolio;
// import com.portfolio.api.model.Stock;

// class StockRepositoryTest {

//     private StockRepository stockRepository;
//     private PortfolioRepository portfolioRepository;

//     @BeforeEach
//     void setup() {
//         stockRepository = Mockito.mock(StockRepository.class);
//         portfolioRepository = Mockito.mock(PortfolioRepository.class);
//     }

//     @Test
//     void testSaveAndFindById() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setId(1L);
//         portfolio.setName("Tech Portfolio");

//         Stock stock = new Stock();
//         stock.setId(10L);
//         stock.setName("Apple Inc.");
//         stock.setShortTicketCode("AAPL");
//         stock.setPurchasePrice(150.0);
//         stock.setQuantity(10.0);
//         stock.setPurchaseDate(LocalDate.of(2024, 1, 1));
//         stock.setLastKnownPrice(new BigDecimal("175.00"));
//         stock.setLastPriceUpdated(LocalDate.of(2024, 1, 5));
//         stock.setPortfolio(portfolio);

//         when(stockRepository.save(any(Stock.class))).thenReturn(stock);
//         when(stockRepository.findById(10L)).thenReturn(Optional.of(stock));

//         Stock saved = stockRepository.save(stock);
//         assertNotNull(saved);
//         assertEquals(10L, saved.getId());

//         Stock found = stockRepository.findById(10L).orElse(null);
//         assertNotNull(found);
//         assertEquals("Apple Inc.", found.getName());
//         assertEquals("AAPL", found.getShortTicketCode());
//         assertEquals(150.0, found.getPurchasePrice());
//         assertEquals(10.0, found.getQuantity());
//         assertEquals(1L, found.getPortfolio().getId());
//     }

//     @Test
//     void testFindByPortfolioId() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setId(2L);
//         portfolio.setName("Growth Portfolio");

//         Stock stock1 = new Stock();
//         stock1.setId(1L);
//         stock1.setShortTicketCode("GOOGL");
//         stock1.setPortfolio(portfolio);

//         Stock stock2 = new Stock();
//         stock2.setId(2L);
//         stock2.setShortTicketCode("MSFT");
//         stock2.setPortfolio(portfolio);

//         List<Stock> mockList = Arrays.asList(stock1, stock2);

//         when(stockRepository.findByPortfolioId(2L)).thenReturn(mockList);

//         List<Stock> stocks = stockRepository.findByPortfolioId(2L);

//         assertEquals(2, stocks.size());
//         assertTrue(stocks.stream().anyMatch(s -> s.getShortTicketCode().equals("GOOGL")));
//         assertTrue(stocks.stream().anyMatch(s -> s.getShortTicketCode().equals("MSFT")));
//     }

//     @Test
//     void testDelete() {
//         Stock stock = new Stock();
//         stock.setId(5L);

//         doNothing().when(stockRepository).delete(stock);
//         when(stockRepository.findById(5L)).thenReturn(Optional.empty());

//         stockRepository.delete(stock);

//         assertFalse(stockRepository.findById(5L).isPresent());
//     }
// }
