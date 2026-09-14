/**
 * Web controllers for the Hello sample application.
 *
 * Includes the MVC controller for the welcome view and the REST controller
 * that exposes the greeting in JSON format.
 */
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

/**
 * MVC controller that handles the welcome page.
 *
 * @property greetingService service responsible for building the greeting.
 */
@Controller
class HelloController(
    private val greetingService: GreetingService
) {
    
    /**
     * Handles GET requests to the root path.
     *
     * @param model Spring MVC model where attributes for the view are added.
     * @param name name of the person to greet; defaults to an empty string.
     * @param lang optional language code explicitly requested.
     * @param acceptLanguage value of the HTTP Accept-Language header, if present.
     * @return the name of the "welcome" view.
     */
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

/**
 * REST controller that exposes the greeting as a JSON API.
 *
 * @property greetingService service responsible for building the greeting.
 */
@RestController
class HelloApiController(
    private val greetingService: GreetingService
) {
    
    /**
     * Handles GET requests to `/api/hello` and returns the greeting as JSON.
     *
     * @param name name of the person to greet; defaults to "World".
     * @param lang optional language code explicitly requested.
     * @param acceptLanguage value of the HTTP Accept-Language header, if present.
     * @return map containing the message, language, time of day, and timestamp.
     */
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
