package com.portfolio.api.respository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.portfolio.api.model.Stocks;

public interface StockRepository extends JpaRepository<Stocks, Long> {

}
