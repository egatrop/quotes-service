package com.yivanou.quotes.ws;

import com.yivanou.quotes.config.WSServerProperties;
import com.yivanou.quotes.service.dto.PriceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PricePublisher {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private final WSServerProperties serverProperties;

    public void publish(PriceEvent event) {
        messagingTemplate.convertAndSend(serverProperties.getTopicName(), event);
    }
}
