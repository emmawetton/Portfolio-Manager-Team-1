package com.portfolio.api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UpdatePortfolioRequest {

    @NotBlank(message = "Portfolio name cannot be empty")
    private String name;

    private String description;
}