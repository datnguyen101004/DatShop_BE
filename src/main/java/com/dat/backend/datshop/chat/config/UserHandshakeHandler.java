package com.dat.backend.datshop.chat.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;

@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    protected Principal determineUser(org.springframework.web.socket.WebSocketSession session,
                                      org.springframework.web.socket.WebSocketHandler wsHandler,
                                      java.util.Map<String, Object> attributes) {

        // Lấy ra email từ session attributes
        String email = (String) session.getAttributes().get("email");
        if (email != null) {
            // Tạo một Principal với email làm tên người dùng
            return () -> email;
        } else {
            // Nếu không có email, trả về null hoặc có thể ném ra ngoại lệ tùy theo logic của bạn
            return null;
        }
    }
}
