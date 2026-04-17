package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UpdateStockRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFieldsAndGettersSetters() {
        UpdateStockRequest request = new UpdateStockRequest();

        Double quantity = 10.0;
        Double purchasePrice = 150.0;

        request.setQuantity(quantity);
        request.setPurchasePrice(purchasePrice);

        assertEquals(quantity, request.getQuantity());
        assertEquals(purchasePrice, request.getPurchasePrice());
    }

    @Test
    void testValidationFailsWhenQuantityIsInvalid() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setQuantity(-5.0); // invalid
        request.setPurchasePrice(100.0);

        Set<ConstraintViolation<UpdateStockRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean quantityViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity"));

        assertTrue(quantityViolation);
    }

    @Test
    void testValidationFailsWhenPurchasePriceIsInvalid() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setQuantity(10.0);
        request.setPurchasePrice(0.0); // invalid

        Set<ConstraintViolation<UpdateStockRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean priceViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("purchasePrice"));

        assertTrue(priceViolation);
    }

    @Test
    void testValidationFailsWhenFieldsAreNull() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setQuantity(null);
        request.setPurchasePrice(null);

        Set<ConstraintViolation<UpdateStockRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean quantityNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity"));

        boolean priceNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("purchasePrice"));

        assertTrue(quantityNullViolation);
        assertTrue(priceNullViolation);
    }

    @Test
    void testValidationPassesWhenFieldsAreValid() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setQuantity(10.0);
        request.setPurchasePrice(200.0);

        Set<ConstraintViolation<UpdateStockRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateStockRequest r1 = new UpdateStockRequest();
        r1.setQuantity(10.0);
        r1.setPurchasePrice(150.0);

        UpdateStockRequest r2 = new UpdateStockRequest();
        r2.setQuantity(10.0);
        r2.setPurchasePrice(150.0);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setQuantity(5.0);

        assertNotNull(request.toString());
        assertTrue(request.toString().contains("5.0"));
    }
}
