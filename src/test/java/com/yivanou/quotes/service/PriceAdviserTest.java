package com.yivanou.quotes.service;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.service.dto.PriceEvent;
import com.yivanou.quotes.service.impl.PriceAdviser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PriceAdviserTest {

    private static final Integer THRESHOLD = 10;

    private final ServiceProperties properties = ServiceProperties.builder()
            .hotPriceThreshold(THRESHOLD)
            .historyPeriod(0)
            .quotesBuffer(0)
            .hotPriceInterval(0)
            .build();

    private final PriceAdviser adviser = new PriceAdviser(properties);

    @Test
    public void whenCheck_givenLowChangeAndEmptyPreviousChange_thenReturnNotHotEvent() {
        // given
        final String isin = UUID.randomUUID().toString();
        final BigDecimal previousPrice = BigDecimal.valueOf(100);
        final BigDecimal currentPrice = BigDecimal.valueOf(109);

        // when
        final PriceEvent event = adviser.checkPrise(isin, currentPrice, previousPrice, null);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getIsHot()).isFalse();
    }

    @Test
    public void whenCheck_givenHighChangeAndEmptyPreviousChange_thenReturnHotEvent() {
        // given
        final String isin = UUID.randomUUID().toString();
        final BigDecimal previousPrice = BigDecimal.valueOf(100);
        final BigDecimal currentPrice = BigDecimal.valueOf(110);

        // when
        final PriceEvent event = adviser.checkPrise(isin, currentPrice, previousPrice, null);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getTrend()).isEqualTo("UP");
        assertThat(event.getChange()).isEqualTo(BigDecimal.TEN.setScale(1));
        assertThat(event.getIsHot()).isTrue();
    }

    @Test
    public void whenCheck_givenHighChangeAndNotEmptyHighPreviousChange_thenReturnNotHotEvent() {
        // given
        final String isin = UUID.randomUUID().toString();
        final BigDecimal previousPrice = BigDecimal.valueOf(100);
        final BigDecimal currentPrice = BigDecimal.valueOf(110);
        final PriceEvent previousEvent = PriceEvent.builder()
                .trend("DOWN")
                .change(BigDecimal.valueOf(15))
                .isin(isin)
                .build();

        // when
        final PriceEvent event = adviser.checkPrise(isin, currentPrice, previousPrice, previousEvent);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getIsHot()).isFalse();
    }

    @Test
    public void whenCheck_givenHighChangeAndNotEmptyLowPreviousChange_thenReturnHotEvent() {
        // given
        final String isin = UUID.randomUUID().toString();
        final BigDecimal previousPrice = BigDecimal.valueOf(100);
        final BigDecimal currentPrice = BigDecimal.valueOf(90);
        final PriceEvent previousEvent = PriceEvent.builder()
                .trend("DOWN")
                .change(BigDecimal.valueOf(9))
                .isin(isin)
                .build();

        // when
        final PriceEvent event = adviser.checkPrise(isin, currentPrice, previousPrice, previousEvent);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getTrend()).isEqualTo("DOWN");
        assertThat(event.getChange()).isEqualTo(BigDecimal.TEN.setScale(1));
        assertThat(event.getIsHot()).isTrue();
    }

    @Test
    public void whenCheck_givenLowChangeAndNotEmptyLowPreviousChange_thenReturnNotHotEvent() {
        // given
        final String isin = UUID.randomUUID().toString();
        final BigDecimal previousPrice = BigDecimal.valueOf(100);
        final BigDecimal currentPrice = BigDecimal.valueOf(109);
        final PriceEvent previousEvent = PriceEvent.builder()
                .trend("DOWN")
                .change(BigDecimal.valueOf(9))
                .isin(isin)
                .build();

        // when
        final PriceEvent event = adviser.checkPrise(isin, currentPrice, previousPrice, previousEvent);

        assertThat(event).isNotNull();
        assertThat(event.getIsHot()).isFalse();
    }

    @Test
    public void whenCheck_givenLowChangeAndNotEmptyHighPreviousChange_thenReturnNotHotEvent() {
        // given
        final String isin = UUID.randomUUID().toString();
        final BigDecimal previousPrice = BigDecimal.valueOf(100);
        final BigDecimal currentPrice = BigDecimal.valueOf(109);
        final PriceEvent previousEvent = PriceEvent.builder()
                .trend("DOWN")
                .change(BigDecimal.valueOf(11))
                .isin(isin)
                .build();

        // when
        final PriceEvent event = adviser.checkPrise(isin, currentPrice, previousPrice, previousEvent);

        assertThat(event).isNotNull();
        assertThat(event.getIsHot()).isFalse();
    }
}