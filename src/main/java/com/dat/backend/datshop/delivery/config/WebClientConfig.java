package com.dat.backend.datshop.delivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${ghn.apiUrl}")
    private String ghn_url;

    @Bean
    public WebClient webClient() {  
        return WebClient.builder()
                .baseUrl(ghn_url)
                .build();
    }
}
