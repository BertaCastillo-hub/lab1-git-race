/**
 * MVC tests for [HelloController] and [HelloApiController] using MockMvc.
 *
 * These tests verify the behavior of the web layer, including view resolution,
 * model attributes, and JSON responses.
 */
package es.unizar.webeng.hello.controller

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [HelloController::class, HelloApiController::class])
@Import(es.unizar.webeng.hello.service.GreetingService::class)
class HelloControllerMVCTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Tests that the home page is returned with the default message and language.
     */
    @Test
    fun `should return home page with default message`() {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("language", equalTo("en")))
            .andExpect(model().attribute("message", containsString("Good ")))
    }
    
    /**
     * Tests that the home page is returned with a personalized message and language.
     */
    @Test
    fun `should return home page with personalized message`() {
        mockMvc.perform(get("/").param("name", "Developer").param("lang", "es"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
                .andExpect(model().attribute("message", containsString("Developer!")))
                .andExpect(model().attribute("language", equalTo("es")))
            .andExpect(model().attribute("name", equalTo("Developer")))
    }

    /**
     * Tests that the query parameter `lang` takes precedence over the `Accept-Language` header.
     */
    @Test
    fun `query language takes precedence over Accept-Language`() {
        mockMvc.perform(get("/").param("lang", "en").header("Accept-Language", "es"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("language", equalTo("en")))
            .andExpect(model().attribute("message", containsString("Good ")))
    }

    /**
     * Tests that the `Accept-Language` header is used when the `lang` parameter is absent.
     */
    @Test
    fun `should use Accept-Language when lang is absent`() {
        mockMvc.perform(get("/").header("Accept-Language", "es"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("language", equalTo("es")))
            // We expect 'Buen' because the posibilities are 'Buenos' or 'Buenas'
            .andExpect(model().attribute("message", containsString("Buen")))
    }
    
    /**
     * Tests that the API endpoint returns a JSON response with the expected fields.
     */
    @Test
    fun `should return API response as JSON`() {
        mockMvc.perform(get("/api/hello").param("name", "Test").param("lang", "es"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", containsString("Test!")))
            .andExpect(jsonPath("$.language", equalTo("es")))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}

