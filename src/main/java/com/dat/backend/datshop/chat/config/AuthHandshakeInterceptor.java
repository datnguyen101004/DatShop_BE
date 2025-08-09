package com.dat.backend.datshop.chat.config;

import com.dat.backend.datshop.authentication.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpReq = servletRequest.getServletRequest();
            String authHeader = httpReq.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Kiểm tra tính hợp lệ của token
                // Nếu token hợp lệ, lưu userId vào attributes để HandshakeHandler có thể sử dụng

                // Logic kiểm tra xem token có hơp lệ hay chưa

                // Giả sử token là hợp lệ và tiếp tục kiểm tra xem token có hết hạn hay không
                if (!jwtService.isTokenExpired(token)) {
                    String email = jwtService.extractUsername(token);
                    attributes.put("email", email); // Lưu email vào attributes để có thể sử dụng trong WebSocketHandler
                    return true;
                }
            }
        }
        return false; // từ chối nếu không có hoặc token sai
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
