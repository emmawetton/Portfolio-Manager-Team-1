package com.portfolio.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// Service that calls the external API to get information on value of stocks
// Returns JSON object
// Names of variables/methods may want to be renamed/refactored where necessary

@Service
public class StocksService {
    
    private final RestTemplate restTemplate;

    public StocksService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getStocksValue(String from, String to) {
        String url = "";

        Map response = restTemplate.getForObject(url, Map.class); 

        if (response == null) {
            throw new RuntimeException("No response from API");
        }

        //'values' needs to be changed according to API
        Map<String, Object> values = (Map<String,Object>)response.get("values");
        
        if (values == null || !values.containsKey(to)) {
            throw new RuntimeException("Stock value not found:" + to);
        }

        return ((Number)values.get(to)).doubleValue();
    }

    
}
