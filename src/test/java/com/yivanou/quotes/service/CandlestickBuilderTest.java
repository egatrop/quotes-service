package com.yivanou.quotes.service;

import com.yivanou.quotes.repository.entity.CandleStick;
import com.yivanou.quotes.service.impl.CandlestickBuilder;
import com.yivanou.quotes.ws.dto.quote.QuoteEventData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CandlestickBuilderTest {

    private final CandlestickBuilder builder = new CandlestickBuilder();

    @Test
    public void whenConvert_givenEmptyList_thenFail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.toCandleStick(Collections.emptyList(), ZonedDateTime.now())
        );
    }

    @Test
    public void whenConvert_givenSingleElementList_thenReturnConvertedResult() {
        // given
        final String isin = UUID.randomUUID().toString();
        final ZonedDateTime timeStamp = ZonedDateTime.now();
        final List<QuoteEventData> list = Collections.singletonList(getEvent(isin, BigDecimal.TEN));

        // when
        final CandleStick result = builder.toCandleStick(list, timeStamp);

        // then
        assertThat(result.getOpenTimeEpochSeconds()).isEqualTo(timeStamp.truncatedTo(ChronoUnit.MINUTES).toEpochSecond());
        assertThat(result.getCloseTimeEpochSeconds()).isEqualTo(timeStamp.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).toEpochSecond());
        assertThat(result.getOpenPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getClosePrice()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getHighPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getClosePrice()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void whenConvert_givenMultipleElementList_thenReturnConvertedResult() {
        // given
        final String isin = UUID.randomUUID().toString();
        final ZonedDateTime timeStamp = ZonedDateTime.now();
        final List<QuoteEventData> list = Arrays.asList(
                getEvent(isin, BigDecimal.valueOf(5)),
                getEvent(isin, BigDecimal.valueOf(10)),
                getEvent(isin, BigDecimal.valueOf(2)),
                getEvent(isin, BigDecimal.valueOf(1)),
                getEvent(isin, BigDecimal.valueOf(3))
        );

        // when
        final CandleStick result = builder.toCandleStick(list, timeStamp);

        // then
        assertThat(result.getOpenTimeEpochSeconds()).isEqualTo(timeStamp.truncatedTo(ChronoUnit.MINUTES).toEpochSecond());
        assertThat(result.getCloseTimeEpochSeconds()).isEqualTo(timeStamp.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).toEpochSecond());
        assertThat(result.getOpenPrice()).isEqualTo(BigDecimal.valueOf(5));
        assertThat(result.getClosePrice()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(result.getHighPrice()).isEqualTo(BigDecimal.valueOf(10));
        assertThat(result.getLowPrice()).isEqualTo(BigDecimal.valueOf(1));
    }

    private QuoteEventData getEvent(String isin, BigDecimal price) {
        return new QuoteEventData(isin, price);
    }
}