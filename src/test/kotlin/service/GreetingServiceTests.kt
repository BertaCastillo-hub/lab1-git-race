package es.unizar.webeng.hello.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class GreetingServiceTests {
    @Test
    fun `should choose greeting based on morning time`() {
        val service = serviceAt("2026-09-14T09:00:00Z")

        val greeting = service.greet("Developer", "es", null)

        assertThat(greeting.message).isEqualTo("Buenos días, Developer!")
        assertThat(greeting.language).isEqualTo("es")
        assertThat(greeting.timeOfDay).isEqualTo("morning")
    }

    @Test
    fun `should choose afternoon and evening greetings`() {
        val afternoon = serviceAt("2026-09-14T15:00:00Z").greet("", "en", null)
        val evening = serviceAt("2026-09-14T21:00:00Z").greet("", "en", null)

        assertThat(afternoon.message).isEqualTo("Good afternoon!")
        assertThat(evening.message).isEqualTo("Good evening!")
    }

    @Test
    fun `should prefer lang over Accept-Language`() {
        val greeting = serviceAt("2026-09-14T09:00:00Z")
            .greet("", "en", "es;q=1.0")

        assertThat(greeting.language).isEqualTo("en")
        assertThat(greeting.message).isEqualTo("Good morning!")
    }

    @Test
    fun `should use first supported language from Accept-Language`() {
        val greeting = serviceAt("2026-09-14T09:00:00Z")
            .greet("", null, "fr-FR,es;q=0.9")

        assertThat(greeting.language).isEqualTo("es")
        assertThat(greeting.message).isEqualTo("Buenos días!")
    }

    @Test
    fun `should fall back to English for unsupported language`() {
        val greeting = serviceAt("2026-09-14T09:00:00Z")
            .greet("", "fr", "es")

        assertThat(greeting.language).isEqualTo("en")
    }

    private fun serviceAt(instant: String): GreetingService {
        val clock = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC)
        return GreetingService(clock)
    }
}
