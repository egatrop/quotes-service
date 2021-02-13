package com.yivanou.quotes.ws.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public abstract class AbstractWSClient<T> extends WebSocketClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Class<T> type;

    public AbstractWSClient(URI serverUri, Class<T> type) {
        super(serverUri);
        this.type = type;
    }

    @Override
    public void onMessage(String s) {
        try {
            log.debug("Incoming message: " + s);
            processParsedObject(getMapper().readValue(s, type));
        } catch (JsonProcessingException e) {
            log.error("Unable to parse data from " + s, e);
        }
    }

    abstract void processParsedObject(T object);


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        // no operation
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        // no operation
    }

    @Override
    public void onError(Exception e) {
        log.error("Error occurred:", e);
    }

    protected ObjectMapper getMapper() {
        return MAPPER;
    }
}
