package com.portfolio.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.api.model.Portfolio;

@SpringBootTest
@Transactional
class PortfolioRepositoryTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void testSaveAndFindById() {
        Portfolio portfolio = new Portfolio();
        portfolio.setName("Tech Portfolio");
        portfolio.setDescription("A portfolio of tech stocks");
        portfolio.setCreatedDate(LocalDate.of(2024, 1, 1));

        Portfolio saved = portfolioRepository.save(portfolio);

        assertNotNull(saved.getId());

        Portfolio found = portfolioRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Tech Portfolio", found.getName());
        assertEquals("A portfolio of tech stocks", found.getDescription());
        assertEquals(LocalDate.of(2024, 1, 1), found.getCreatedDate());
    }

    @Test
    void testDelete() {
        Portfolio portfolio = new Portfolio();
        portfolio.setName("To Delete");
        portfolio.setCreatedDate(LocalDate.now());

        Portfolio saved = portfolioRepository.save(portfolio);
        Long id = saved.getId();

        portfolioRepository.delete(saved);

        assertFalse(portfolioRepository.findById(id).isPresent());
    }

    @Test
    void testFindAll() {
        Portfolio p1 = new Portfolio();
        p1.setName("Portfolio 1");
        p1.setCreatedDate(LocalDate.now());

        Portfolio p2 = new Portfolio();
        p2.setName("Portfolio 2");
        p2.setCreatedDate(LocalDate.now());

        portfolioRepository.save(p1);
        portfolioRepository.save(p2);

        var all = portfolioRepository.findAll();

        assertEquals(2, all.size());
    }
}
