package com.yivanou.quotes.service;

import com.yivanou.quotes.ws.dto.quote.QuoteEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class QuotesQueue {

    private final BlockingQueue<QuoteEvent> queue = new ArrayBlockingQueue<>(1024);

    public List<QuoteEvent> pollAll() {
        final List<QuoteEvent> events = Arrays.asList(queue.toArray(new QuoteEvent[0]));
        queue.clear();
        return events;
    }

    public void addAll(List<QuoteEvent> event) {
        queue.addAll(event);
    }

    public int size() {
        return queue.size();
    }
}
