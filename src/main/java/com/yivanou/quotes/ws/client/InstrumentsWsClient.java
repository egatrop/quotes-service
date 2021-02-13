package com.yivanou.quotes.ws.client;

import com.yivanou.quotes.config.WSProperties;
import com.yivanou.quotes.service.IInstrumentsService;
import com.yivanou.quotes.ws.dto.instrument.InstrumentEvent;

import java.net.URI;

public class InstrumentsWsClient extends AbstractWSClient<InstrumentEvent> {

    private final IInstrumentsService service;

    public InstrumentsWsClient(WSProperties properties, IInstrumentsService service) {
        super(URI.create("ws://" + properties.getHost() + ":" + properties.getPort() + "/" + properties.getStreamInstruments()), InstrumentEvent.class);
        this.service = service;
    }

    @Override
    void processParsedObject(InstrumentEvent event) {
        switch (event.getType()) {
            case ADD: service.addInstrument(event.getData().getIsin()); break;
            case DELETE: service.deleteInstrument(event.getData().getIsin()); break;
        }
    }
}
