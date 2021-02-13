package com.yivanou.quotes.service;

import com.yivanou.quotes.ws.dto.quote.QuoteEvent;

import java.time.ZonedDateTime;

public interface IQuotesService {

    void flushToRepository(ZonedDateTime timestamp);

    void accumulate(QuoteEvent event);
}
