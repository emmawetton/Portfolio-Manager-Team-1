package com.portfolio.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Stocks")
public class Stock {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;
    private double purchasePrice;
    private double quantity; 
    private LocalDate purchaseDate; 
    private String shortTicketCode;

    // Many-to-one relationship with Portfolio, with a join column named "portfolio_id" 
    // which is a foreign key referencing the Portfolio entity.
    // This means that each Stock is associated with one Portfolio, 
    // and the portfolio_id column in the Stocks table will store the ID of the associated Portfolio.
    // The @ManyToOne annotation indicates that many Stock entities can be associated with one Portfolio entity,
    // and the @JoinColumn annotation specifies the name of the foreign key column in the Stocks table that references the Portfolio entity.
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private BigDecimal lastKnownPrice;
    private LocalDateTime lastPriceUpdated;


}
