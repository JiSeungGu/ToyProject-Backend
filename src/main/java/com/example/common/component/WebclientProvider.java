package com.example.common.component;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebclientProvider {
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }
}
