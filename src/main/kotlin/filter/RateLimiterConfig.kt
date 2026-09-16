package es.unizar.webeng.hello.config

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.bucket4j.caffeine.CaffeineProxyManager
import io.github.bucket4j.distributed.proxy.AsyncProxyManager
import io.github.bucket4j.distributed.remote.RemoteBucketState
import org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.time.Duration

@Configuration
class RateLimiterConfig {

    // Bean to manage the in-memory storage and expiration of buckets using Caffeine.
    @Bean
    fun caffeineProxyManager(): AsyncProxyManager<String> {
        // Cafeine cache size limited to 1000 of buckets
        @Suppress("UNCHECKED_CAST")
        val caffeineBuilder = Caffeine.newBuilder()
            .maximumSize(1000) as Caffeine<String, RemoteBucketState>
        return CaffeineProxyManager(caffeineBuilder, Duration.ofMinutes(1)).asAsync()
    }

    // Declarative configuration of the route with the RateLimiter filter integrated
    @Bean
    fun gatewayRouter(): RouterFunction<ServerResponse> {
        return route("rate_limit_route")
            .GET("/**", http())
            .filter(
                rateLimit { config ->
                    config.setCapacity(10)
                        .setPeriod(Duration.ofMinutes(1))
                        .setKeyResolver { req ->
                            req.remoteAddress()
                                .map { it.address.hostAddress }
                                .orElse("127.0.0.1")
                        }
                }
            )
            .build()
    }
}