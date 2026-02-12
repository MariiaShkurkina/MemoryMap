package ru.tbank.education.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.geo")
public class GeoProperties {
    private String baseUrl;
    private String userAgent;
    private int defaultLimit = 8;
}