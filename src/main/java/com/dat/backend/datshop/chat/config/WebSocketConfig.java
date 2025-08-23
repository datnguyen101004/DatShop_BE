package com.dat.backend.datshop.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final UserHandshakeHandler userHandshakeHandler;
    private final AuthChannelInterceptor authChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cho phép client đăng ký nhận tin nhắn từ các topic
        config.enableSimpleBroker("/topic", "/queue");

        // Cho phép client gửi tin nhắn đến các topic
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        // Đăng ký endpoint cho WebSocket
        registry.addEndpoint("/ws")
                .setHandshakeHandler(userHandshakeHandler) // Gắn principal cho WebSocket
                .addInterceptors(authHandshakeInterceptor) // Xác thực người dùng trước khi kết nối
                .setAllowedOriginPatterns("*"); // Cho phép tất cả các origin truy cập
        // Thêm SockJS để hỗ trợ fallback nếu WebSocket không khả dụng
        registry.addEndpoint("/ws")
//                .setHandshakeHandler(userHandshakeHandler)
//                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor); // Xác thực người dùng cho các tin nhắn gửi lên server
    }
}
