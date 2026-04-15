package com.portfolio.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "portfolios")
public class Portfolio {
    // properties
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String name;

    @OneToMany
    private List<Stocks> stocks;
    
    }