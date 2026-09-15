package es.unizar.webeng.hello.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Servlet filter that limits the number of requests accepted from each client IP address.
 *
 * The filter allows a maximum of 10 requests per IP address within a one-minute
 * sliding time window. Requests older than one minute are removed from the
 * in-memory request history before checking the limit.
 *
 * When the limit is exceeded, the filter responds with HTTP status 429
 * and prevents the request from reaching the rest of the filter chain.
 *
 * Request history is stored in memory and is maintained separately for each
 * client IP address. The concurrent collections used by this class allow the
 * filter to be safely accessed by multiple requests concurrently.
 */
@Component
@Order(1)
class RateLimitFilter : Filter {

    // In-memory log: associates each IP with a list of timestamps of its requests
    private val requestLog = ConcurrentHashMap<String, CopyOnWriteArrayList<Instant>>()

     /**
     * Filters incoming HTTP requests and applies the rate limit.
     *
     * Requests older than one minute are removed from the client's request
     * history. If the client has already made 10 or more requests during
     * the current one-minute window, the request is rejected with HTTP 429.
     * Otherwise, the current request is recorded and the filter chain continues.
     *
     * @param request the incoming servlet request
     * @param response the servlet response
     * @param chain the filter chain used to continue request processing
     */
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val res = response as HttpServletResponse

        // Identify the client IP.
        val clientIp = req.remoteAddr
        val now = Instant.now()
        val oneMinuteAgo = now.minusSeconds(60)

        // Recover the request history for that IP (or create a new one if it doesn't exist)
        val requests = requestLog.computeIfAbsent(clientIp) { CopyOnWriteArrayList() }

        // Clean that IP history by removing requests older than 1 minute
        requests.removeIf { it.isBefore(oneMinuteAgo) }

        // Check if it exceeds the limit (10 requests)
        if (requests.size >= 20) {
            // Return error 429 Too Many Requests
            res.status = 429
            res.contentType = "application/json"
            res.writer.write("""{"error": "Too Many Requests", "message": "Rate limit exceeded. Try again in a minute."}""")
            
            // We stop the request from getting to the controllers
            return
        }

        // It is valid, we log the current request and continue the chain
        requests.add(now)
        chain.doFilter(request, response)
    }
}