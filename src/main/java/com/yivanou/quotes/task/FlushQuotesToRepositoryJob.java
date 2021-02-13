package com.yivanou.quotes.task;

import com.yivanou.quotes.service.IQuotesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class FlushQuotesToRepositoryJob {

    @Autowired
    private IQuotesService quoteService;

    /**
     * The method called each minute to flush data from queue
     * to repository
     * */
    @Scheduled(cron = "*/10 * * * * *")
    public void runJob() {
        quoteService.flushToRepository(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).minusMinutes(1));
    }
}
