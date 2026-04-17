package com.portfolio.api.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // ---------------------------------------------------------
    // Embedded Test Controller
    // ---------------------------------------------------------
    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/notfound")
        public void throwNotFound() {
            throw new GlobalExceptionHandler.ResourceNotFoundException("Portfolio not found");
        }

        @GetMapping("/illegal")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument");
        }

        @GetMapping("/general")
        public void throwGeneral() {
            throw new RuntimeException("Unexpected error");
        }

        @PostMapping("/validate")
        public void validateInput(@Valid @RequestBody TestRequest request) {}

        static class TestRequest {
            @NotBlank(message = "Name is required")
            public String name;
        }
    }

    // ---------------------------------------------------------
    // Setup MockMvc manually (REQUIRED for Spring Boot 4)
    // ---------------------------------------------------------
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())          // Register controller
                .setControllerAdvice(new GlobalExceptionHandler()) // Register exception handler
                .build();
    }

    // ---------------------------------------------------------
    // Tests
    // ---------------------------------------------------------

    @Test
    void testResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/test/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Portfolio not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid argument"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testValidationException() throws Exception {
        String json = """
                { "name": "" }
                """;

        mockMvc.perform(post("/test/validate")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGeneralException() throws Exception {
        mockMvc.perform(get("/test/general"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Something went wrong"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
