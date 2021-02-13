package com.yivanou.quotes.service;

import com.yivanou.quotes.service.dto.CandleStickDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInstrumentsService {

    boolean isValid(String isin);

    void addInstrument(String isin);

    void deleteInstrument(String isin);

    Map<String, BigDecimal> getLatestPrices();

    List<CandleStickDto> getHistory(String isin);
}
