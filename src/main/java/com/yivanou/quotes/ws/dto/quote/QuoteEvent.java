package com.yivanou.quotes.ws.dto.quote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteEvent {
    private QuoteEventType type;
    private QuoteEventData data;
}
