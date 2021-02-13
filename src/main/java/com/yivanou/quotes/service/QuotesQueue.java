package com.yivanou.quotes.service;

import com.yivanou.quotes.ws.dto.quote.QuoteEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class QuotesQueue {

    private final ConcurrentLinkedQueue<QuoteEvent> queue = new ConcurrentLinkedQueue<>();

    public List<QuoteEvent> pollAll() {
        final List<QuoteEvent> events = Arrays.asList(queue.toArray(new QuoteEvent[0]));
        queue.clear();
        return events;
    }

    public void addAll(List<QuoteEvent> event) {
        queue.addAll(event);
    }
}
