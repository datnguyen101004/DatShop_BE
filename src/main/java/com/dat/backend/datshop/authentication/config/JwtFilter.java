package com.dat.backend.datshop.authentication.config;

import com.dat.backend.datshop.authentication.service.JwtService;
import com.dat.backend.datshop.authentication.service.UserAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        var jwt = authHeader.substring(7);
        try {
            var username = jwtService.extractUsername(jwt);
            if (username != null) {
                UserDetails userDetails = userAuthService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(usernamePasswordAuthenticationToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            } else {
                throw new RuntimeException("Invalid JWT token");  // Nếu không tìm thấy username, ném ngoại lệ
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token");  // Ném ngoại lệ khi có lỗi trong quá trình xử lý
        }

        filterChain.doFilter(request, response);
    }
}
