package com.yivanou.quotes.service.impl;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.service.dto.PriceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class PriceAdviser {

    @Autowired
    private final ServiceProperties properties;

    public PriceEvent checkPrise(String isin, BigDecimal currentPrice, BigDecimal previousPrice, PriceEvent previousEvent) {
        if (isin == null || currentPrice == null || previousPrice == null) {
            throw new IllegalArgumentException("arguments must not be null");
        }

        final BigDecimal change = calculateChange(currentPrice, previousPrice);

        if ((previousEvent == null || !isMoreThantThreshold(previousEvent.getChange())) && isMoreThantThreshold(change)) {
            return PriceEvent.builder()
                    .isin(isin)
                    .change(change.abs())
                    .trend(getTrend(change))
                    .isHot(true)
                    .build();
        }

        return PriceEvent.builder()
                .isin(isin)
                .change(change.abs())
                .trend(getTrend(change))
                .isHot(false)
                .build();
    }

    private BigDecimal calculateChange(BigDecimal currentPrice, BigDecimal previousPrice) {
        return currentPrice.subtract(previousPrice).divide(previousPrice, 3, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)).setScale(1);
    }

    private boolean isMoreThantThreshold(BigDecimal change) {
        return change.abs().compareTo(BigDecimal.valueOf(properties.getHotPriceThreshold())) >= 0;
    }

    private String getTrend(BigDecimal change) {
        return change.compareTo(BigDecimal.ZERO) >= 0
                ? "UP"
                : "DOWN";
    }
}
