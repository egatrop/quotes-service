package com.yivanou.quotes.service.impl;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.repository.ICandleRepository;
import com.yivanou.quotes.service.IInstrumentsService;
import com.yivanou.quotes.service.IQuotesQueue;
import com.yivanou.quotes.service.IQuotesService;
import com.yivanou.quotes.ws.dto.quote.QuoteEvent;
import com.yivanou.quotes.ws.dto.quote.QuoteEventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class QuotesService implements IQuotesService {

    private final List<QuoteEvent> tempEvents = new ArrayList<>();

    @Autowired
    private final IInstrumentsService instrumentsService;

    @Autowired
    private final IQuotesQueue quotesQueue;

    @Autowired
    private final ServiceProperties properties;

    @Autowired
    private final CandlestickBuilder converter;

    @Autowired
    private final ICandleRepository repository;

    public void flushToRepository(ZonedDateTime timestamp) {
        quotesQueue.pollAll().stream()
                .map(QuoteEvent::getData)
                .filter(eventData -> instrumentsService.exists(eventData.getIsin()))
                .collect(groupingBy(QuoteEventData::getIsin))
                .forEach((key, value) -> repository.persist(key, converter.toCandleStick(value, timestamp)));
    }

    public void accumulate(QuoteEvent event) {
        if (tempEvents.size() <= properties.getQuotesBuffer()) {
            tempEvents.add(event);
        } else {
            quotesQueue.addAll(new ArrayList<>(tempEvents));
            tempEvents.clear();
        }
    }
}
