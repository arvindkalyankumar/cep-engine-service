package com.sebis.cepengineservice.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebis.cepengineservice.messaging.dto.SpanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    @KafkaListener(topics = "${kafka.topic.span-info}")
    public void receive(String payload) {
        LOGGER.info("received payload='{}'", payload);
        List<SpanDTO> spans = null;
        try {
            spans = new ObjectMapper().readValue(payload, new TypeReference<List<SpanDTO>>() {});
        } catch (IOException e) {
            LOGGER.info("Failed to deserialize the payload");
        }
        // ToDo process received spans
    }
}
