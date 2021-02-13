package com.yivanou.quotes.task;

import com.yivanou.quotes.service.IInstrumentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotPriceHeartbeatJob {

    @Autowired
    private IInstrumentsService service;

    /**
     * The method called each minute to flush data from queue
     * to repository
     * */
    @Scheduled(cron = "0 */1 * * * *")
    public void runJob() {
        service.recalculateHotPrices();
    }
}
