package com.yivanou.quotes.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "spring.partners.ws.client")
public class WSClientProperties {
    private String host;
    private String port;
    private String streamInstruments;
    private String streamQuotes;
}
