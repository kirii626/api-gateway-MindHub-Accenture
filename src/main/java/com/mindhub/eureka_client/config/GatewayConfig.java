package com.mindhub.eureka_client.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .uri("lb://user-microservice"))
                .route("products-service", r-> r.path("/products/**")
                        .uri("lb://product-microservice"))
                .route("order-service", r -> r.path("/orders/**")
                        .filters(f -> f.addRequestHeader("X-Origin", "Gateway"))
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
