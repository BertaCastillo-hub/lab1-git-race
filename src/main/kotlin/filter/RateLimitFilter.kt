package es.unizar.webeng.hello.filter

import io.github.bucket4j.Bucket
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * Servlet filter that limits the number of requests accepted from each client IP address
 * using Bucket4j.
 *
 * The filter allows a maximum of 10 requests per IP address within a one-minute
 * window.
 *
 * When the limit is exceeded, the filter responds with HTTP status 429
 * and prevents the request from reaching the rest of the filter chain.
 *
 * Request history is stored in memory and is maintained separately for each
 * client IP address. The concurrent collection used by this class allow the
 * filter to be safely accessed by multiple requests concurrently.
 */
@Component
@Order(1)
// To be able to disable the filter in tests
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = ["rate.limit.enabled"],
    havingValue = "true",
    matchIfMissing = true // Active in production by default
)
class RateLimitFilter : Filter {

    // Map storing a Bucket per client IP address
    private val buckets = ConcurrentHashMap<String, Bucket>()

    private fun createNewBucket(): Bucket {
        return Bucket.builder()
            .addLimit { limit -> limit.capacity(10).refillGreedy(10, Duration.ofMinutes(1)) }
            .build()
    }

     /**
     * Filters incoming HTTP requests and applies the rate limit.
     *
     * If the client has already made 10 or more requests during
     * the current one-minute window, the request is rejected with HTTP 429.
     * Otherwise, it extracts a token from their bucket and the filter chain continues.
     *
     * @param request the incoming servlet request
     * @param response the servlet response
     * @param chain the filter chain used to continue request processing
     */
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest

        val path = req.requestURI

        // Does not count as requests for the rate limit, so we just continue the chain
        if (path.startsWith("/actuator") || 
            path.startsWith("/css") || 
            path.startsWith("/js") || 
            path.startsWith("/assets") || 
            path.startsWith("/webjars")) {
            chain.doFilter(request, response)
            return
        }

        val res = response as HttpServletResponse

        // Identify the client IP and get or create its bucket
        val clientIp = req.remoteAddr
        val bucket = buckets.computeIfAbsent(clientIp) { createNewBucket() }

        // Each request consumes one token from the bucket. If the bucket is empty,
        // the limit has been exceeded
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response)
        } else {
            // Return error 429 Too Many Requests
            res.status = 429
            res.contentType = "application/json"
            res.writer.write("""{"error": "Too Many Requests", "message": "Rate limit exceeded. Try again in a minute."}""")
        }
    }

    /**
     * Clears all recorded buckets
     */
    fun reset() {
        buckets.clear()
    }
}