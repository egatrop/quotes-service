package com.yivanou.quotes.service;

import com.yivanou.quotes.config.ServiceProperties;
import com.yivanou.quotes.repository.CandleRepository;
import com.yivanou.quotes.ws.dto.quote.QuoteEvent;
import com.yivanou.quotes.ws.dto.quote.QuoteEventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class QuotesService {

    private final List<QuoteEvent> tempEvents = new ArrayList<>();

    @Autowired
    private InstrumentsService instrumentsService;

    @Autowired
    private QuotesQueue quotesQueue;

    @Autowired
    private ServiceProperties properties;

    @Autowired
    private CandlestickBuilder converter;

    @Autowired
    private CandleRepository repository;

    public void flushToRepository(ZonedDateTime timestamp) {
        final Set<String> availableInstruments = instrumentsService.getAvailableInstruments();

        final List<QuoteEvent> events = quotesQueue.pollAll();

        events.stream()
                .map(QuoteEvent::getData)
                .filter(eventData -> availableInstruments.contains(eventData.getIsin()))
                .collect(groupingBy(QuoteEventData::getIsin))
                .forEach((key, value) -> repository.persist(key, converter.toCandleStick(value, timestamp)));
    }

    public void add(QuoteEvent event) {
        if (tempEvents.size() <= properties.getQuotesBuffer()) {
            tempEvents.add(event);
        } else {
            quotesQueue.addAll(new ArrayList<>(tempEvents));
            tempEvents.clear();
        }
    }
}
