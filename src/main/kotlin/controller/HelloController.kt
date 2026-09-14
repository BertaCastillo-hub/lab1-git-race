package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.service.GreetingService
import org.springframework.http.MediaType
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Controller
class HelloController(
    private val greetingService: GreetingService
) {
    
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(required = false) lang: String?,
        @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE, required = false) acceptLanguage: String?
    ): String {
        val greeting = greetingService.greet(name, lang, acceptLanguage)
        model.addAttribute("message", greeting.message)
        model.addAttribute("name", name)
        model.addAttribute("language", greeting.language)
        model.addAttribute("timeOfDay", greeting.timeOfDay)
        return "welcome"
    }
}

@RestController
class HelloApiController(
    private val greetingService: GreetingService
) {
    
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(
        @RequestParam(defaultValue = "World") name: String,
        @RequestParam(required = false) lang: String?,
        @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE, required = false) acceptLanguage: String?
    ): Map<String, String> {
        val greeting = greetingService.greet(name, lang, acceptLanguage)
        return mapOf(
            "message" to greeting.message,
            "language" to greeting.language,
            "timeOfDay" to greeting.timeOfDay,
            "timestamp" to java.time.Instant.now().toString()
        )
    }
}
