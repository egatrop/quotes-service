package com.yivanou.quotes.ws.client;

import com.yivanou.quotes.config.WSProperties;
import com.yivanou.quotes.service.QuotesService;
import com.yivanou.quotes.ws.dto.quote.QuoteEvent;

import java.net.URI;

public class QuotesWsClient extends AbstractWSClient<QuoteEvent> {

    private final QuotesService service;

    public QuotesWsClient(WSProperties properties, QuotesService service) {
        super(URI.create("ws://" + properties.getHost() + ":" + properties.getPort() + "/" + properties.getStreamQuotes()), QuoteEvent.class);
        this.service = service;
    }

    @Override
    void processParsedObject(QuoteEvent event) {
        service.add(event);
    }
}
