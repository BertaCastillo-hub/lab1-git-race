/**
 * Unit tests for [GreetingService].
 *
 * These tests verify language resolution, time-of-day greeting selection,
 * and fallback behavior using a fixed [Clock].
 */
package es.unizar.webeng.hello.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class GreetingServiceTests {
    /**
     * Tests that the morning greeting is chosen correctly for Spanish.
     */
    @Test
    fun `should choose greeting based on morning time`() {
        val service = serviceAt("2026-09-14T09:00:00Z")

        val greeting = service.greet("Developer", "es", null)

        assertThat(greeting.message).isEqualTo("Buenos días, Developer!")
        assertThat(greeting.language).isEqualTo("es")
        assertThat(greeting.timeOfDay).isEqualTo("morning")
    }

    /**
     * Tests that the afternoon and night greetings are chosen correctly for English.
     */
    @Test
    fun `should choose afternoon and night greetings`() {
        val afternoon = serviceAt("2026-09-14T15:00:00Z").greet("", "en", null)
        val night = serviceAt("2026-09-14T21:00:00Z").greet("", "en", null)

        assertThat(afternoon.message).isEqualTo("Good afternoon!")
        assertThat(night.message).isEqualTo("Good night!")
    }

    /**
     * Tests that the `lang` parameter takes precedence over the `Accept-Language` header.
     */
    @Test
    fun `should prefer lang over Accept-Language`() {
        val greeting = serviceAt("2026-09-14T09:00:00Z")
            .greet("", "en", "es;q=1.0")

        assertThat(greeting.language).isEqualTo("en")
        assertThat(greeting.message).isEqualTo("Good morning!")
    }

    /**
     * Tests that the first supported language from the `Accept-Language` header is used.
     */
    @Test
    fun `should use first supported language from Accept-Language`() {
        val greeting = serviceAt("2026-09-14T09:00:00Z")
            .greet("", null, "fr-FR,es;q=0.9")

        assertThat(greeting.language).isEqualTo("es")
        assertThat(greeting.message).isEqualTo("Buenos días!")
    }

    /**
     * Tests that an unsupported language falls back to English.
     */
    @Test
    fun `should fall back to English for unsupported language`() {
        val greeting = serviceAt("2026-09-14T09:00:00Z")
            .greet("", "fr", "es")

        assertThat(greeting.language).isEqualTo("en")
    }

    /**
     * Creates a [GreetingService] with a fixed clock at the given instant.
     *
     * @param instant the instant to fix the clock at, in ISO-8601 format.
     * @return a [GreetingService] using the fixed clock.
     */
    private fun serviceAt(instant: String): GreetingService {
        val clock = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC)
        return GreetingService(clock)
    }
}
