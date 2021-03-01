package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(spec -> spec
                        .path("/dealers")
                        .filters(fspec -> fspec
                                .setPath("/api/v1/dealers"))
                        .uri("lb://dealers"))
                .route(spec -> spec
                        .path("/dealers/count")
                        .filters(fspec -> fspec
                                .setPath("/api/v1/dealers/count"))
                        .uri("lb://dealers"))
                .route(spec -> spec
                        .path("/vehicles")
                        .filters(fspec -> fspec
                                .setPath("/api/v1/vehicles"))
                        .uri("lb://vehicles"))
                .route(spec -> spec
                        .path("/vehicles/count")
                        .filters(fspec -> fspec
                                .requestRateLimiter(rlc -> rlc
                                        .setRateLimiter(redisRateLimiter()))
                                .setPath("/api/v1/vehicles/count"))
                        .uri("lb://vehicles"))
                .build();
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(5, 5);
    }

    @Bean
    MapReactiveUserDetailsService authentication() {
        return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
                .username("sinfo")
                .password("sinfo")
                .authorities("USER")
                .build());
    }

    @Bean
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/vehicles").authenticated()
                        .anyExchange().permitAll())
                .build();
    }

}

