/**
 * Unit tests for [HelloController] and [HelloApiController].
 *
 * These tests directly invoke the controller methods with a mocked [Model]
 * and a fixed [Clock] to verify the returned view name, model attributes,
 * and API response map.
 */
package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.service.GreetingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ui.Model
import org.springframework.ui.ExtendedModelMap
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class HelloControllerUnitTests {
    private lateinit var controller: HelloController
    private lateinit var model: Model
    
    @BeforeEach
    fun setup() {
        val clock = Clock.fixed(Instant.parse("2026-09-14T09:00:00Z"), ZoneOffset.UTC)
        controller = HelloController(GreetingService(clock))
        model = ExtendedModelMap()
    }
    
    /**
     * Tests that the welcome view is returned with the default message and language.
     */
    @Test
    fun `should return welcome view with default message`() {
        val view = controller.welcome(model, "", null, null)
        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message")).isEqualTo("Good morning!")
        assertThat(model.getAttribute("name")).isEqualTo("")
        assertThat(model.getAttribute("language")).isEqualTo("en")
    }
    
    /**
     * Tests that the welcome view is returned with a personalized message and language.
     */
    @Test
    fun `should return welcome view with personalized message`() {
        val view = controller.welcome(model, "Developer", "es", null)
        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message")).isEqualTo("Buenos días, Developer!")
        assertThat(model.getAttribute("name")).isEqualTo("Developer")
    }
    
    /**
     * Tests that the API response contains the expected message and a timestamp.
     */
    @Test
    fun `should return API response with timestamp`() {
        val clock = Clock.fixed(Instant.parse("2026-09-14T21:00:00Z"), ZoneOffset.UTC)
        val apiController = HelloApiController(GreetingService(clock))
        val response = apiController.helloApi("Test", "en", null)
        
        assertThat(response).containsKey("message")
        assertThat(response).containsKey("timestamp")
        assertThat(response["message"]).isEqualTo("Good night, Test!")
        assertThat(response["timestamp"]).isNotNull()
    }
}
