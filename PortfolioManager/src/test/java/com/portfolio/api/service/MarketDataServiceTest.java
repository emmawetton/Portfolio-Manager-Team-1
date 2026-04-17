package com.portfolio.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

public class MarketDataServiceTest {

    private MarketDataService service;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() throws Exception {
        service = new MarketDataService();

        // Inject API key
        Field apiKeyField = MarketDataService.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(service, "TEST_KEY");

        // Replace RestTemplate with a mock
        restTemplate = Mockito.mock(RestTemplate.class);
        Field restTemplateField = MarketDataService.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(service, restTemplate);
    }

    // ---------------------------------------------------------
    // getCurrentPrice Tests
    // ---------------------------------------------------------

    @Test
    void testGetCurrentPriceSuccess() {
        String json = """
                { "price": "150.25" }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        BigDecimal price = service.getCurrentPrice("AAPL");

        assertEquals(new BigDecimal("150.25"), price);
    }

    @Test
    void testGetCurrentPriceInvalidSymbol() {
        String json = """
                { "code": 400, "message": "Invalid symbol" }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        assertThrows(IllegalArgumentException.class,
                () -> service.getCurrentPrice("INVALID"));
    }

    @Test
    void testGetCurrentPriceEmptyPrice() {
        String json = """
                { "price": "" }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        assertThrows(IllegalArgumentException.class,
                () -> service.getCurrentPrice("AAPL"));
    }

    // ---------------------------------------------------------
    // getStockName Tests
    // ---------------------------------------------------------

    @Test
    void testGetStockNameSuccess() {
        String json = """
                {
                  "data": [
                    { "name": "Apple Inc." }
                  ]
                }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        String name = service.getStockName("AAPL");

        assertEquals("Apple Inc.", name);
    }

    @Test
    void testGetStockNameInvalidSymbol() {
        String json = """
                { "code": 400, "message": "Invalid symbol" }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        assertThrows(IllegalArgumentException.class,
                () -> service.getStockName("INVALID"));
    }

    @Test
    void testGetStockNameEmptyData() {
        String json = """
                { "data": [] }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        assertThrows(IllegalArgumentException.class,
                () -> service.getStockName("AAPL"));
    }

    // ---------------------------------------------------------
    // getHistoricalPrices Tests
    // ---------------------------------------------------------

    @Test
    void testGetHistoricalPricesSuccess() {
        String json = """
                {
                  "values": [
                    { "datetime": "2024-01-01", "close": "150.00" },
                    { "datetime": "2023-12-01", "close": "145.50" }
                  ]
                }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        List<MarketDataService.MonthlyPrice> prices =
                service.getHistoricalPrices("AAPL", 2);

        assertEquals(2, prices.size());
        assertEquals("2024-01-01", prices.get(0).getDate());
        assertEquals(new BigDecimal("150.00"), prices.get(0).getPrice());
    }

    @Test
    void testGetHistoricalPricesInvalidSymbol() {
        String json = """
                { "code": 400, "message": "Invalid symbol" }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(json);

        List<MarketDataService.MonthlyPrice> prices =
                service.getHistoricalPrices("INVALID", 5);

        assertTrue(prices.isEmpty());
    }

    @Test
    void testGetHistoricalPricesException() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("API down"));

        List<MarketDataService.MonthlyPrice> prices =
                service.getHistoricalPrices("AAPL", 5);

        assertTrue(prices.isEmpty());
    }
}
