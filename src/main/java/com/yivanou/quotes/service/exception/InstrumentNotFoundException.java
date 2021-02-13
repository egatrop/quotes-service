package com.yivanou.quotes.service.exception;

public class InstrumentNotFoundException extends RuntimeException {
    public InstrumentNotFoundException(String msg) {
        super(msg);
    }
}
