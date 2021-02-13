package com.yivanou.quotes.service.converter;


import com.yivanou.quotes.repository.entity.CandleStick;
import com.yivanou.quotes.service.dto.CandleStickDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CandleStickConverter {

    public CandleStickDto toDto(CandleStick entity) {
        return CandleStickDto.builder()
                .openTimeStamp(ZonedDateTime.ofInstant(Instant.ofEpochSecond(entity.getOpenTimeEpochSeconds()), ZoneId.of("UTC")))
                .closeTimeStamp(ZonedDateTime.ofInstant(Instant.ofEpochSecond(entity.getCloseTimeEpochSeconds()), ZoneId.of("UTC")))
                .openPrice(entity.getOpenPrice())
                .closePrice(entity.getClosePrice())
                .lowPrice(entity.getLowPrice())
                .highPrice(entity.getHighPrice())
                .build();
    }
}
