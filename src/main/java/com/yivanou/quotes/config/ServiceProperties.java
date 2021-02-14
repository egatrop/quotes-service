package com.yivanou.quotes.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
@ConfigurationProperties(prefix = "spring.service")
public class ServiceProperties {
    private Integer quotesBuffer;
    private Integer historyPeriod;
    private Integer hotPriceInterval;
    private Integer hotPriceThreshold;
}
