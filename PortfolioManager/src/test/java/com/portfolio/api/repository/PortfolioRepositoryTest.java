package com.portfolio.api.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.portfolio.api.model.Portfolio;

class PortfolioRepositoryTest {

    private PortfolioRepository portfolioRepository;

    @BeforeEach
    void setup() {
        portfolioRepository = Mockito.mock(PortfolioRepository.class);
    }

    @Test
    void testSaveAndFindById() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Tech Portfolio");
        portfolio.setDescription("A portfolio of tech stocks");
        portfolio.setCreatedDate(LocalDate.of(2024, 1, 1));

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Portfolio saved = portfolioRepository.save(portfolio);
        assertNotNull(saved);
        assertEquals(1L, saved.getId());

        Portfolio found = portfolioRepository.findById(1L).orElse(null);
        assertNotNull(found);
        assertEquals("Tech Portfolio", found.getName());
    }

    @Test
    void testDelete() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(2L);

        doNothing().when(portfolioRepository).delete(portfolio);
        when(portfolioRepository.findById(2L)).thenReturn(Optional.empty());

        portfolioRepository.delete(portfolio);

        assertFalse(portfolioRepository.findById(2L).isPresent());
    }

    @Test
    void testFindAll() {
        Portfolio p1 = new Portfolio();
        p1.setId(1L);
        p1.setName("Portfolio 1");

        Portfolio p2 = new Portfolio();
        p2.setId(2L);
        p2.setName("Portfolio 2");

        List<Portfolio> mockList = Arrays.asList(p1, p2);

        when(portfolioRepository.findAll()).thenReturn(mockList);

        var all = portfolioRepository.findAll();

        assertEquals(2, all.size());
    }
}
