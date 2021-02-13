package com.yivanou.quotes.service;

import com.yivanou.quotes.repository.entity.CandleStick;
import com.yivanou.quotes.ws.dto.quote.QuoteEventData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class CandlestickBuilder {

    public CandleStick toCandleStick(List<QuoteEventData> quotes, ZonedDateTime timestamp) {
        if (quotes.isEmpty()) throw new IllegalArgumentException("quotes should not be empty");

        final ZonedDateTime openTime = timestamp.truncatedTo(ChronoUnit.MINUTES);
        final ZonedDateTime closeTime = openTime.plusMinutes(1);

        return quotes.size() == 1
                ? getCandleStickForSingle(quotes, openTime, closeTime)
                : getCandleStickForList(quotes, openTime, closeTime);
    }

    private CandleStick getCandleStickForSingle(List<QuoteEventData> quotes, ZonedDateTime openTime, ZonedDateTime closeTime) {
        final BigDecimal price = quotes.get(0).getPrice();
        return CandleStick.builder()
                .openTimeEpochSeconds(openTime.toEpochSecond())
                .closeTimeEpochSeconds(closeTime.toEpochSecond())
                .openPrice(price)
                .closePrice(price)
                .lowPrice(price)
                .highPrice(price)
                .build();
    }

    private CandleStick getCandleStickForList(List<QuoteEventData> quotes, ZonedDateTime openTime, ZonedDateTime closeTime) {
        final BigDecimal openPrice = quotes.get(0).getPrice();
        final BigDecimal closePrice = quotes.get(quotes.size() - 1).getPrice();
        final BigDecimal lowPrice = quotes.stream()
                .map(QuoteEventData::getPrice)
                .min(BigDecimal::compareTo)
                .orElseThrow(IllegalStateException::new);
        final BigDecimal highPrice = quotes.stream()
                .map(QuoteEventData::getPrice)
                .max(BigDecimal::compareTo)
                .orElseThrow(IllegalStateException::new);

        return CandleStick.builder()
                .openTimeEpochSeconds(openTime.toEpochSecond())
                .closeTimeEpochSeconds(closeTime.toEpochSecond())
                .openPrice(openPrice)
                .closePrice(closePrice)
                .lowPrice(lowPrice)
                .highPrice(highPrice)
                .build();
    }
}
