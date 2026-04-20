package com.portfolio.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.portfolio.api.model.Portfolio;

//@Repository indicates that this interface is a Spring Data repository, 
// which will be automatically implemented by Spring Data JPA to provide CRUD operations for the Portfolio entity.
// The PortfolioRepository interface extends JpaRepository, which is a JPA specific extension of the Repository interface.
// By extending JpaRepository, PortfolioRepository inherits several methods for working with Portfolio persistence,
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
   

}
    

