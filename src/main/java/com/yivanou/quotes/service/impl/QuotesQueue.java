package com.yivanou.quotes.service.impl;

import com.yivanou.quotes.service.IQuotesQueue;
import com.yivanou.quotes.ws.dto.quote.QuoteEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class QuotesQueue implements IQuotesQueue {

    private final LinkedBlockingQueue<QuoteEvent> queue = new LinkedBlockingQueue<>();

    @Override
    public List<QuoteEvent> pollAll() {
        final List<QuoteEvent> result = new ArrayList<>();
        queue.drainTo(result);
        return result;
    }

    @Override
    public void addAll(List<QuoteEvent> event) {
        queue.addAll(event);
    }
}
