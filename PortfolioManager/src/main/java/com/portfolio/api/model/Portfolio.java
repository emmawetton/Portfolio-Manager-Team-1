package com.portfolio.api.model;
// jakarta.persistence imports all JPA annotations (Entity, Id, GeneratedValue, etc.)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.CascadeType;
import java.time.LocalDate;
import java.util.List;

//@Data generates getters, setters, toString, equals, and hashCode methods

@Data
@Entity
@Table(name = "portfolios")
public class Portfolio {
    // properties
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;
    private String description;
    private LocalDate createdDate;
    
    

    // One-to-many relationship with Stock, mapped by the "portfolio" field in Stock, with cascade and orphan removal which 
    // means that when a Portfolio is deleted, all associated Stocks will also be deleted, and if a Stock is removed from the Portfolio's 
    // stock list, it will be deleted from the database as well.
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;
    
    // methods

   
}

