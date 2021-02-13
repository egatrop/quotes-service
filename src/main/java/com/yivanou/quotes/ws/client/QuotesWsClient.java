package com.yivanou.quotes.ws.client;

import com.yivanou.quotes.config.WSClientProperties;
import com.yivanou.quotes.service.IQuotesService;
import com.yivanou.quotes.ws.dto.quote.QuoteEvent;

import java.net.URI;

public class QuotesWsClient extends AbstractWSClient<QuoteEvent> {

    private final IQuotesService service;

    public QuotesWsClient(WSClientProperties properties, IQuotesService service) {
        super(URI.create("ws://" + properties.getHost() + ":" + properties.getPort() + "/" + properties.getStreamQuotes()), QuoteEvent.class);
        this.service = service;
    }

    @Override
    void processParsedObject(QuoteEvent event) {
        service.accumulate(event);
    }
}
