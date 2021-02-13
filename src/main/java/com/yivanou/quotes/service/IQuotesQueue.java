package com.yivanou.quotes.service;

import com.yivanou.quotes.ws.dto.quote.QuoteEvent;

import java.util.List;

public interface IQuotesQueue {
    List<QuoteEvent> pollAll();

    void addAll(List<QuoteEvent> event);
}
