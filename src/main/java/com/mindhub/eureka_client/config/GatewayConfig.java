package com.mindhub.eureka_client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://user-microservice"))
                .route("user-service", r -> r.path("/api/user/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://user-microservice"))
                .route("admin-service", r -> r.path("/api/admin/**")
                        .filters( f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://user-microservice"))
                .route("internal-user-service", r -> r.path("/internal/user/**")
                        .uri("lb://user-microservice"))

                .route("products-user-service", r-> r.path("/api/product/**")
                        .uri("lb://product-microservice"))
                .route("products-admin-service", r -> r.path("/api/product-admin/**")
                        .filters( f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://product-microservice"))

                .route("order-user-service", r -> r.path("/api/order-user/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://order-microservice"))
                .route("order-admin-service", r -> r.path("/api/order-admin/**")
                        .filters( f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://order-microservice"))

//               /*
//               .route("get-user-service", r -> r.method("GET")
//                        .and()
//                        .path("/users/**")
//                        .uri("lb://user-microservice"))
//
//                .route("write-order-service", r -> r.method("POST", "PUT", "DELETE")
//                        .and()
//                        .path("/orders/**")
//                        .uri("lb://order-microservice"))
//
//                .route("default-route", r -> r.path("/**")
//                        .uri("lb://product-microservice"))*/
                .build();
    }
}
