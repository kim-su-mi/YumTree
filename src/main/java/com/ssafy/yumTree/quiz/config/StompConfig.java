package com.ssafy.yumTree.quiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    public StompConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000")
                // ws:// 가 아닌 http:// 앤드포인트를 사용할 수 있게 해주는 sockJS 라이브러리르 통한 요청을 허용하는 설정.
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /publish/1 형태로 메시지 발행해야 함을 설정
        // /publish로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/publish");
//      /topic/1형태로 메시지를 수신(subscribe) 해야 함을 설정
        registry.enableSimpleBroker("/topic");
    }

    // 웹소캣 요청 (connect, subscribe, disconnect) 등의 요청시에는 http header 등 http메시지를 넣어올 수 있고, 이를 interceptor 를 통해 가로채 토큰 등을 검증할 수 있음
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
