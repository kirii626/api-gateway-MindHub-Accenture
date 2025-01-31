package com.mindhub.eureka_client.config;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtils jwtUtils;


    public JwtAuthenticationFilter(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!exchange.getRequest().getPath().toString().startsWith("/api/auth")) {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    if (!jwtUtils.isTokenExpired(token)) {
                        Claims claims = jwtUtils.parseClaims(token);

                        String username = claims.getSubject();
                        String role = claims.get("role", String.class);

                        if ((exchange.getRequest().getPath().toString().startsWith("/api/admin")
                                | exchange.getRequest().getPath().toString().startsWith("/api/product-admin")
                                | exchange.getRequest().getPath().toString().startsWith("api/order-admin"))
                                && !"ADMIN".equals(role)) {
                            return onError(exchange, "Access Denied: Requires ADMIN role", HttpStatus.FORBIDDEN);
                        }

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(builder -> builder
                                        .header("X-Username", username)
                                        .header("X-Role", role)
                                )
                                .build();

                        return chain.filter(modifiedExchange);
                    } else {
                        return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
                    }
                } catch (Exception e) {
                    return onError(exchange, "JWT Token validation failed", HttpStatus.UNAUTHORIZED);
                }
            } else {
                return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
