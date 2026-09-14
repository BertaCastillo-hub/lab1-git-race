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

@WebMvcTest(HelloController::class, HelloApiController::class)
@Import(es.unizar.webeng.hello.service.GreetingService::class)
class HelloControllerMVCTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return home page with default message`() {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("language", equalTo("en")))
            .andExpect(model().attribute("message", containsString("Good ")))
    }
    
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

            @Test
            fun `query language takes precedence over Accept-Language`() {
            mockMvc.perform(get("/").param("lang", "en").header("Accept-Language", "es"))
                .andExpect(status().isOk)
                .andExpect(model().attribute("language", equalTo("en")))
                .andExpect(model().attribute("message", containsString("Good ")))
            }

            @Test
            fun `should use Accept-Language when lang is absent`() {
            mockMvc.perform(get("/").header("Accept-Language", "es"))
                .andExpect(status().isOk)
                .andExpect(model().attribute("language", equalTo("es")))
                .andExpect(model().attribute("message", containsString("Buenas ")))
            }
    
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

