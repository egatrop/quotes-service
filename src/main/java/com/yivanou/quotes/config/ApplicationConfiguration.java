package com.yivanou.quotes.config;

import com.yivanou.quotes.service.IInstrumentsService;
import com.yivanou.quotes.service.IQuotesService;
import com.yivanou.quotes.ws.client.InstrumentsWsClient;
import com.yivanou.quotes.ws.client.QuotesWsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class ApplicationConfiguration implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private final WSClientProperties clientProperties;

    @Autowired
    private final WSServerProperties serverProperties;

    @Bean(initMethod = "connect", destroyMethod = "close")
    public QuotesWsClient getQuotesWsClient(@Autowired IQuotesService service) {
        return new QuotesWsClient(clientProperties, service);
    }

    @Bean(initMethod = "connect", destroyMethod = "close")
    public InstrumentsWsClient getInstrumentsWsClient(@Autowired IInstrumentsService service) {
        return new InstrumentsWsClient(clientProperties, service);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/" + serverProperties.getPrefix())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }
}
