package com.portfolio.api.repository;

import com.portfolio.api.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// @Repository indicates that this interface is a Spring Data repository, 
// which will be automatically implemented by Spring Data JPA to provide CRUD operations for the Stock entity
// The StockRepository interface extends JpaRepository, which is a JPA specific extension of the Repository interface.
// By extending JpaRepository, StockRepository inherits several methods for working with Stock persistence,
// The findByPortfolioId method is a custom query method that retrieves a list of Stock entities based on the portfolioId.

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByPortfolioId(Long portfolioId);
}
