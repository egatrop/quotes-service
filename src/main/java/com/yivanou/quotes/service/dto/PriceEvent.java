package com.yivanou.quotes.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEvent {
    private String isin;
    private String trend;
    private BigDecimal change;
    private Boolean isHot;
}
