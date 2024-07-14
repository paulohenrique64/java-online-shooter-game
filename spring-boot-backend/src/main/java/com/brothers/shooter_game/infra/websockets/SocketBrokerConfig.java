package com.brothers.shooter_game.infra.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.data.mongo.config.annotation.web.reactive.EnableMongoWebSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class SocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/log");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/socket").addInterceptors(new HttpHandshakeInterceptor());
//        registry.addEndpoint("/socket").addInterceptors(new HttpHandshakeInterceptor()).withSockJS();
        registry.addEndpoint("/socket").setAllowedOrigins("*");
        registry.addEndpoint("/socket").setAllowedOrigins("*").withSockJS();
    }
}