package com.yivanou.quotes.service;

import com.yivanou.quotes.service.dto.PriceEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceAdviser {

    public PriceEvent checkPrise(String isin, BigDecimal current, BigDecimal previous) {
        return null;
    }
}
