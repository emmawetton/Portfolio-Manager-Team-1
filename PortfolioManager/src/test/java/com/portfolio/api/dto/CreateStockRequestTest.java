package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CreateStockRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFieldsAndGettersSetters() {
        CreateStockRequest request = new CreateStockRequest();

        String symbol = "AAPL";
        Double quantity = 5.0;
        Double purchasePrice = 150.0;
        LocalDate purchaseDate = LocalDate.of(2024, 1, 10);

        request.setSymbol(symbol);
        request.setQuantity(quantity);
        request.setPurchasePrice(purchasePrice);
        request.setPurchaseDate(purchaseDate);

        assertEquals(symbol, request.getSymbol());
        assertEquals(quantity, request.getQuantity());
        assertEquals(purchasePrice, request.getPurchasePrice());
        assertEquals(purchaseDate, request.getPurchaseDate());
    }

    @Test
    void testValidationFailsWhenSymbolIsBlank() {
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol(""); // invalid
        request.setQuantity(10.0);
        request.setPurchasePrice(100.0);
        request.setPurchaseDate(LocalDate.now());

        Set<ConstraintViolation<CreateStockRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean hasSymbolViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("symbol"));

        assertTrue(hasSymbolViolation);
    }

    @Test
    void testValidationFailsForInvalidNumbers() {
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("AAPL");
        request.setQuantity(-1.0); // invalid
        request.setPurchasePrice(0.0); // invalid
        request.setPurchaseDate(LocalDate.now());

        Set<ConstraintViolation<CreateStockRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean quantityViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity"));

        boolean priceViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("purchasePrice"));

        assertTrue(quantityViolation);
        assertTrue(priceViolation);
    }

    @Test
    void testValidationFailsWhenPurchaseDateIsNull() {
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("AAPL");
        request.setQuantity(10.0);
        request.setPurchasePrice(100.0);
        request.setPurchaseDate(null); // invalid

        Set<ConstraintViolation<CreateStockRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean dateViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate"));

        assertTrue(dateViolation);
    }

    @Test
    void testValidationPassesWhenAllFieldsAreValid() {
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("AAPL");
        request.setQuantity(10.0);
        request.setPurchasePrice(150.0);
        request.setPurchaseDate(LocalDate.now());

        Set<ConstraintViolation<CreateStockRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        CreateStockRequest r1 = new CreateStockRequest();
        r1.setSymbol("AAPL");
        r1.setQuantity(10.0);
        r1.setPurchasePrice(150.0);
        r1.setPurchaseDate(LocalDate.now());

        CreateStockRequest r2 = new CreateStockRequest();
        r2.setSymbol("AAPL");
        r2.setQuantity(10.0);
        r2.setPurchasePrice(150.0);
        r2.setPurchaseDate(r1.getPurchaseDate());

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        CreateStockRequest request = new CreateStockRequest();
        request.setSymbol("AAPL");

        assertNotNull(request.toString());
        assertTrue(request.toString().contains("AAPL"));
    }
}
