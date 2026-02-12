package ru.tbank.education.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class GeoClientConfig {

    private final GeoProperties props;

    @Bean
    public RestClient nominatimRestClient() {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, props.getUserAgent())
                .build();
    }
}