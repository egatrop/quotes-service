package com.yivanou.quotes.service;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.service.impl.CandlestickBuilder;
import com.yivanou.quotes.service.impl.QuotesService;
import com.yivanou.quotes.ws.dto.quote.QuoteEvent;
import com.yivanou.quotes.ws.dto.quote.QuoteEventData;
import com.yivanou.quotes.ws.dto.quote.QuoteEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuotesServiceTest {

    @Mock
    private IInstrumentsService instrumentsService;

    @Mock
    private IQuotesQueue quotesQueue;

    @Mock
    private ServiceProperties properties;

    @Mock
    private CandlestickBuilder converter;

    @Mock
    private ICandleRepository repository;

    @InjectMocks
    private QuotesService quotesService;

    @Test
    public void testFlushToRepository() {
        // given
        final ZonedDateTime timeStamp = ZonedDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES).withZoneSameInstant(ZoneId.of("UTC"));
        final QuoteEvent e1 = getEvent();
        final QuoteEvent e2 = getEvent();
        e2.getData().setIsin(e1.getData().getIsin());
        final QuoteEvent e3 = getEvent();
        final QuoteEvent e4 = getEvent();
        when(quotesQueue.pollAll()).thenReturn(Arrays.asList(e1, e2, e3, e4));
        when(instrumentsService.exists(e1.getData().getIsin())).thenReturn(true);
        when(instrumentsService.exists(e2.getData().getIsin())).thenReturn(true);
        when(instrumentsService.exists(e3.getData().getIsin())).thenReturn(true);
        when(instrumentsService.exists(e4.getData().getIsin())).thenReturn(false);

        // when
        quotesService.flushToRepository(timeStamp);

        // then
        verify(converter).toCandleStick(Arrays.asList(e1.getData(), e2.getData()), timeStamp);
        verify(converter).toCandleStick(Collections.singletonList(e3.getData()), timeStamp);
        verify(converter, times(0)).toCandleStick(Collections.singletonList(e4.getData()), timeStamp);
        verify(repository).persist(eq(e1.getData().getIsin()), any());
        verify(repository).persist(eq(e3.getData().getIsin()), any());
        verify(repository, times(0)).persist(eq(e4.getData().getIsin()), any());
    }

    private QuoteEvent getEvent() {
        QuoteEvent event = new QuoteEvent();
        event.setType(QuoteEventType.QUOTE);
        event.setData(new QuoteEventData(UUID.randomUUID().toString(), BigDecimal.valueOf(new Random().nextDouble())));
        return event;
    }
}
