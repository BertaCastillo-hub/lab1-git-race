package es.unizar.webeng.hello.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
// Restart the context between tests to clear the in-memory map of the filter
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RateLimitFilterTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should allow 10 requests and block the 11th with 429 Too Many Requests`() {
        val url = "http://localhost:$port/"

        // 10 requests should be allowed
        for (i in 1..10) {
            val response = restTemplate.getForEntity(url, String::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        }

        // 11th request should be blocked
        val blockedResponse = restTemplate.getForEntity(url, String::class.java)
        
        assertThat(blockedResponse.statusCode.value()).isEqualTo(429)
        assertThat(blockedResponse.body).contains("Too Many Requests")
        assertThat(blockedResponse.body).contains("Rate limit exceeded")
    }

    @Test
    fun `should not apply rate limit to excluded paths like actuator`() {
        val url = "http://localhost:$port/actuator/health"

        // 15 requests should be allowed to excluded paths
        for (i in 1..15) {
            val response = restTemplate.getForEntity(url, String::class.java)
            
            // All should return 200 OK, ignoring the limit of 10
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        }
    }
}