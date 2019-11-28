package com.flowdim.demo.reactivekafkawebsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component()
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(ReactiveWebSocketHandler.class);

    @Autowired()
    private KafkaService kafkaService;

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

        return webSocketSession.send(

                // only create the send pipeline after receiving a filterString
                webSocketSession.receive()
                        .log()
                        .map(WebSocketMessage::getPayloadAsText)

                        // this is necessary because we receive the filterString with leading and trailing quotes
                        .map(rawFilterString -> rawFilterString.substring(1, rawFilterString.length() - 1))

                        // map the filterString to a function that loads
                        // flux from our service, filters the data and prepares it for sending
                        .flatMap(this::filteredKafkaTopic)

                        // send the incoming data
                        .map(webSocketSession::textMessage));
    }

    private Flux<String> filteredKafkaTopic(String filterString) {

        return kafkaService.getTestTopicFlux()
                .filter(record -> record.value().contains(filterString))
                .map(record -> {
                            Message message = new Message("[Test] Add message", record.value());

                            try {
                                return json.writeValueAsString(message);
                            } catch (JsonProcessingException e) {
                                return "Error while serializing to JSON";
                            }
                        }
                );
    }
}