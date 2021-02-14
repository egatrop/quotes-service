package com.yivanou.quotes.service.impl;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.repository.entity.CandleStick;
import com.yivanou.quotes.service.converter.CandleStickConverter;
import com.yivanou.quotes.service.dto.CandleStickDto;
import com.yivanou.quotes.service.dto.PriceEvent;
import com.yivanou.quotes.ws.PricePublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrumentsServiceTest {

    @Spy
    private CandleStickConverter converter;

    @Mock
    private ICandleRepository repository;

    @Mock
    private ServiceProperties properties;

    @Mock
    private PriceAdviser hotPriceAdviser;

    @Mock
    private PricePublisher pricePublisher;

    @InjectMocks
    private InstrumentsService instrumentsService;

    @Test
    void whenDeleteInstrumentByIsin_givenIsin_thenRemoveOnlyGivenIsin() {
        // given
        final String isin1 = UUID.randomUUID().toString();
        final String isin2 = UUID.randomUUID().toString();
        instrumentsService.addInstrument(isin1);
        instrumentsService.addInstrument(isin2);

        // when
        instrumentsService.deleteInstrument(isin1);

        // then
        verify(repository).delete(isin1);
        assertThat(instrumentsService.exists(isin1)).isFalse();
        assertThat(instrumentsService.exists(isin2)).isTrue();
    }

    @Test
    void whenGetLastPrices_givenCandlesForSomeInstruments_thenReturnResultForAllInstrumentsWithZeroPriceForMissing() {
        // given
        final String isin1 = UUID.randomUUID().toString();
        final BigDecimal closePrice1 = BigDecimal.valueOf(101);

        final String isin2 = UUID.randomUUID().toString();
        final BigDecimal closePrice2 = BigDecimal.valueOf(102);

        final String isin3 = UUID.randomUUID().toString();

        instrumentsService.addInstrument(isin1);
        instrumentsService.addInstrument(isin2);
        instrumentsService.addInstrument(isin3);

        when(repository.getLastCandleForAll()).thenReturn(new HashMap<String, CandleStick>() {{
            put(isin1, getCandleStick(closePrice1));
            put(isin2, getCandleStick(closePrice2));
        }});

        // when
        final Map<String, BigDecimal> result = instrumentsService.getLatestPrices();

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(isin1)).isEqualTo(closePrice1);
        assertThat(result.get(isin2)).isEqualTo(closePrice2);
        assertThat(result.get(isin3)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void whenCalculateHistory_givenCandlesNotForAllMinutes_thenReturnProperHistory() {
        // when
        final Integer limit = 5;
        final String isin1 = UUID.randomUUID().toString();
        instrumentsService.addInstrument(isin1);
        final ZonedDateTime timeStamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        when(properties.getHistoryPeriod()).thenReturn(limit);
        when(repository.getLastByIsin(isin1, limit)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(6)));
            add(getCandleStick(timeStamp.minusMinutes(3)));
            add(getCandleStick(timeStamp.minusMinutes(2)));
        }});

        // when
        final List<CandleStickDto> result = instrumentsService.getHistory(isin1);

        // then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isEqualTo(result.get(1));
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isNotEqualTo(result.get(2));
        assertThat(result.get(3))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isNotEqualTo(result.get(2));
        assertThat(result.get(3))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isEqualTo(result.get(4));
    }

    @Test
    void whenCalculateHistory_givenOneCandle_thenReturnProperHistory() {
        // when
        final Integer limit = 5;
        final String isin1 = UUID.randomUUID().toString();
        instrumentsService.addInstrument(isin1);
        final ZonedDateTime timeStamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        when(properties.getHistoryPeriod()).thenReturn(limit);
        when(repository.getLastByIsin(isin1, limit)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(6)));
        }});

        // when
        final List<CandleStickDto> result = instrumentsService.getHistory(isin1);

        // then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isEqualTo(result.get(1));
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isEqualTo(result.get(2));
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isEqualTo(result.get(3));
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("openTimeStamp", "closeTimeStamp")
                .isEqualTo(result.get(4));
    }

    @Test
    void whenRecalculateHotPrices_givenHistoryForLessThanFiveMinutes_thenNoEventsPublished() {
        // when
        when(properties.getHotPriceInterval()).thenReturn(5);
        final String isin1 = UUID.randomUUID().toString();
        final String isin2 = UUID.randomUUID().toString();
        final String isin3 = UUID.randomUUID().toString();
        instrumentsService.addInstrument(isin1);
        instrumentsService.addInstrument(isin2);
        instrumentsService.addInstrument(isin3);

        final ZonedDateTime timeStamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        when(repository.getLastByIsin(isin1, 5)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(3)));
            add(getCandleStick(timeStamp.minusMinutes(1)));
            add(getCandleStick(timeStamp));
        }});
        when(repository.getLastByIsin(isin2, 5)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(3)));
            add(getCandleStick(timeStamp));
        }});
        when(repository.getLastByIsin(isin3, 5)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(3)));
            add(getCandleStick(timeStamp.minusMinutes(2)));
            add(getCandleStick(timeStamp));
        }});

        // when
        instrumentsService.recalculateHotPrices();

        // then
        verify(hotPriceAdviser, times(0)).checkPrise(any(), any(), any(), any());
        verify(pricePublisher, times(0)).publish(any());
    }

    @Test
    void whenRecalculateHotPrices_givenHistoryForMoreThanFiveMinutes_thenEventsPublished() {
        // when
        when(properties.getHotPriceInterval()).thenReturn(5);
        final String isin1 = UUID.randomUUID().toString();
        final String isin2 = UUID.randomUUID().toString();
        instrumentsService.addInstrument(isin1);
        instrumentsService.addInstrument(isin2);

        final ZonedDateTime timeStamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        when(repository.getLastByIsin(isin1, 5)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(1)));
            add(getCandleStick(timeStamp));
        }});
        when(repository.getLastByIsin(isin2, 5)).thenReturn(new LinkedList<CandleStick>() {{
            add(getCandleStick(timeStamp.minusMinutes(10)));
            add(getCandleStick(timeStamp));
        }});
        when(hotPriceAdviser.checkPrise(eq(isin2), any(), any(), any())).thenReturn(PriceEvent.builder().isin(isin2).isHot(true).build());

        // when
        instrumentsService.recalculateHotPrices();

        // then
        verify(hotPriceAdviser, times(0)).checkPrise(eq(isin1), any(), any(), any());
        verify(hotPriceAdviser, times(1)).checkPrise(eq(isin2), any(), any(), any());
        verify(pricePublisher, times(0)).publish(argThat((PriceEvent event) -> event.getIsin().equals(isin1)));
        verify(pricePublisher, times(1)).publish(argThat((PriceEvent event) -> event.getIsin().equals(isin2)));
    }

    private CandleStick getCandleStick(ZonedDateTime timeStamp) {
        final CandleStick candleStick = getCandleStick(BigDecimal.valueOf(new Random().nextDouble() * 100));

        candleStick.setOpenTimeEpochSeconds(timeStamp.toEpochSecond());
        candleStick.setCloseTimeEpochSeconds(candleStick.getOpenTimeEpochSeconds() + 60);

        return candleStick;
    }

    private CandleStick getCandleStick(BigDecimal closePrice) {
        final ZonedDateTime timeStamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        return CandleStick.builder()
                .openTimeEpochSeconds(timeStamp.toEpochSecond())
                .closeTimeEpochSeconds(timeStamp.plusMinutes(1).toEpochSecond())
                .openPrice(BigDecimal.valueOf(new Random().nextDouble() * 100))
                .closePrice(closePrice)
                .highPrice(BigDecimal.valueOf(new Random().nextDouble() * 100))
                .lowPrice(BigDecimal.valueOf(new Random().nextDouble() * 100))
                .build();
    }
}
