package com.portfolio.api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class CreateStockRequest {

    @NotBlank(message = "Stock symbol cannot be empty")
    private String symbol;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price must be greater than zero")
    private Double purchasePrice;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;
}
