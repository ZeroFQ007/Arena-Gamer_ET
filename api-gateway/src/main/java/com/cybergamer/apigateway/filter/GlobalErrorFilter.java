package com.cybergamer.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class GlobalErrorFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Instant start = Instant.now();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path = request.getURI().getPath();
        String remoteAddress = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        log.info("[GATEWAY-AUDIT] IN  -> method={} path={} remoteIp={}", method, path, remoteAddress);

        return chain.filter(exchange)
                .doOnSuccess(v -> logResponse(exchange, start, method, path, null))
                .onErrorResume(throwable -> {
                    logResponse(exchange, start, method, path, throwable);
                    return Mono.error(throwable);
                });
    }

    private void logResponse(ServerWebExchange exchange, Instant start, String method, String path, Throwable throwable) {
        ServerHttpResponse response = exchange.getResponse();
        HttpStatusCode statusCode = response.getStatusCode();
        long durationMs = Instant.now().toEpochMilli() - start.toEpochMilli();

        if (throwable != null) {
            log.error("[GATEWAY-AUDIT] ERROR -> method={} path={} durationMs={} error={}",
                    method, path, durationMs, throwable.getMessage());
            return;
        }

        if (statusCode != null && statusCode.isError()) {
            log.warn("[GATEWAY-AUDIT] OUT -> method={} path={} status={} durationMs={}",
                    method, path, statusCode.value(), durationMs);
        } else {
            log.info("[GATEWAY-AUDIT] OUT -> method={} path={} status={} durationMs={}",
                    method, path, statusCode != null ? statusCode.value() : "N/A", durationMs);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}