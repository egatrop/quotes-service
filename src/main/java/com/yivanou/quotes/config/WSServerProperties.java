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
@ConfigurationProperties(prefix = "spring.partners.ws.server")
public class WSServerProperties {
    private String topicName;
    private String prefix;
}
