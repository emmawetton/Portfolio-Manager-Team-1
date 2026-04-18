package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UpdatePortfolioRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFieldsAndGettersSetters() {
        UpdatePortfolioRequest request = new UpdatePortfolioRequest();

        String name = "Updated Portfolio";
        String description = "Updated description";

        request.setName(name);
        request.setDescription(description);

        assertEquals(name, request.getName());
        assertEquals(description, request.getDescription());
    }

    @Test
    void testValidationFailsWhenNameIsBlank() {
        UpdatePortfolioRequest request = new UpdatePortfolioRequest();
        request.setName(""); // invalid
        request.setDescription("Some description");

        Set<ConstraintViolation<UpdatePortfolioRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean hasNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

        assertTrue(hasNameViolation);
    }

    @Test
    void testValidationPassesWhenNameIsValid() {
        UpdatePortfolioRequest request = new UpdatePortfolioRequest();
        request.setName("Valid Name");
        request.setDescription("Description");

        Set<ConstraintViolation<UpdatePortfolioRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdatePortfolioRequest r1 = new UpdatePortfolioRequest();
        r1.setName("Portfolio A");

        UpdatePortfolioRequest r2 = new UpdatePortfolioRequest();
        r2.setName("Portfolio A");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        UpdatePortfolioRequest request = new UpdatePortfolioRequest();
        request.setName("Test");

        assertNotNull(request.toString());
        assertTrue(request.toString().contains("Test"));
    }
}

