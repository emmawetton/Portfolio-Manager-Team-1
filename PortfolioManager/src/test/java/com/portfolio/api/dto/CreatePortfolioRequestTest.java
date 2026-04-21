package com.portfolio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CreatePortfolioRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFieldsAndGettersSetters() {
        CreatePortfolioRequest request = new CreatePortfolioRequest();

        String name = "My Portfolio";
        String description = "A test portfolio";

        request.setName(name);
        request.setDescription(description);

        assertEquals(name, request.getName());
        assertEquals(description, request.getDescription());
    }

    @Test
    void testValidationFailsWhenNameIsBlank() {
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName(""); // invalid
        request.setDescription("Some description");

        Set<ConstraintViolation<CreatePortfolioRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean hasNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

        assertTrue(hasNameViolation);
    }

    @Test
    void testValidationPassesWhenNameIsValid() {
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("Valid Name");
        request.setDescription("Description");

        Set<ConstraintViolation<CreatePortfolioRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        CreatePortfolioRequest r1 = new CreatePortfolioRequest();
        r1.setName("Portfolio A");

        CreatePortfolioRequest r2 = new CreatePortfolioRequest();
        r2.setName("Portfolio A");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("Test");

        assertNotNull(request.toString());
        assertTrue(request.toString().contains("Test"));
    }
}
