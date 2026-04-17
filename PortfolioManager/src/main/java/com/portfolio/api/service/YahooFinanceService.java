package com.portfolio.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class YahooFinanceService {

    @Value("${twelvedata.apikey}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BigDecimal getCurrentPrice(String symbol) {
        try {
            String url = "https://api.twelvedata.com/price?symbol="
                        + symbol + "&apikey=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.has("code")) {
                throw new IllegalArgumentException("Invalid stock symbol: " + symbol);
            }

            String price = root.path("price").asText();
            if (price == null || price.isEmpty()) {
                throw new IllegalArgumentException("Could not fetch price for symbol: " + symbol);
            }
            return new BigDecimal(price);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not fetch price for symbol: " + symbol);
        }
    }

    public String getStockName(String symbol) {
        try {
            String url = "https://api.twelvedata.com/stocks?symbol="
                        + symbol + "&apikey=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.has("code")) {
                throw new IllegalArgumentException("Invalid stock symbol: " + symbol);
            }

            JsonNode data = root.path("data");
            if (data.isEmpty()) {
                throw new IllegalArgumentException("Invalid stock symbol: " + symbol);
            }
            return data.get(0).path("name").asText();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not fetch name for symbol: " + symbol);
        }
    }

    public List<MonthlyPrice> getHistoricalPrices(String symbol, int months) {
        try {
            String url = "https://api.twelvedata.com/time_series?symbol="
                        + symbol
                        + "&interval=1month"
                        + "&outputsize=" + months
                        + "&apikey=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.has("code")) {
                return new ArrayList<>();
            }

            JsonNode values = root.path("values");
            List<MonthlyPrice> prices = new ArrayList<>();

            for (JsonNode node : values) {
                String date = node.path("datetime").asText();
                String closePrice = node.path("close").asText();
                prices.add(new MonthlyPrice(date, new BigDecimal(closePrice)));
            }
            return prices;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static class MonthlyPrice {
        private String date;
        private BigDecimal price;

        public MonthlyPrice(String date, BigDecimal price) {
            this.date = date;
            this.price = price;
        }

        public String getDate() { return date; }
        public BigDecimal getPrice() { return price; }
    }
}