package com.yivanou.quotes.config;

import com.yivanou.quotes.service.IInstrumentsService;
import com.yivanou.quotes.service.IQuotesService;
import com.yivanou.quotes.ws.client.InstrumentsWsClient;
import com.yivanou.quotes.ws.client.QuotesWsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    @Autowired
    private WSProperties properties;

    @Bean(initMethod = "connect", destroyMethod = "close")
    public QuotesWsClient getQuotesWsClient(@Autowired IQuotesService service) {
        return new QuotesWsClient(properties, service);
    }

    @Bean(initMethod = "connect", destroyMethod = "close")
    public InstrumentsWsClient getInstrumentsWsClient(@Autowired IInstrumentsService service) {
        return new InstrumentsWsClient(properties, service);
    }
}
