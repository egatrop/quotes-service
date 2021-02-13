package com.yivanou.quotes.ws.dto.instrument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentEvent {
    private InstrumentEventType type;
    private InstrumentEventData data;
}
