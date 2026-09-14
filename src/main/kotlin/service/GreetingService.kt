/**
 * Service layer for the Hello sample application.
 *
 * Provides the logic to build a greeting based on the requested language,
 * the HTTP Accept-Language header, and the current time of day.
 */
package es.unizar.webeng.hello.service

import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalTime
import java.util.Locale

/**
 * Represents a greeting message along with its language and time of day.
 *
 * @property message the greeting text to display.
 * @property language the language code used for the greeting ("en" or "es").
 * @property timeOfDay the part of the day ("morning", "afternoon", or "night").
 */
data class Greeting(
    val message: String,
    val language: String,
    val timeOfDay: String
)

/**
 * Service that generates greetings.
 *
 * @property clock clock used to determine the current time; defaults to the system default zone.
 */
@Service
class GreetingService(
    private val clock: Clock = Clock.systemDefaultZone()
) {
    /**
     * Builds a greeting for the given name, requested language, and Accept-Language header.
     *
     * @param name name of the person to greet.
     * @param requestedLanguage explicit language code requested by the client, if any.
     * @param acceptLanguage value of the HTTP Accept-Language header, if present.
     * @return a [Greeting] containing the message, resolved language, and time of day.
     */
    fun greet(name: String, requestedLanguage: String?, acceptLanguage: String?): Greeting {
        val language = resolveLanguage(requestedLanguage, acceptLanguage)
        val timeOfDay = resolveTimeOfDay(LocalTime.now(clock))
        val message = messageFor(language, timeOfDay, name)
        return Greeting(message, language, timeOfDay)
    }

    /**
     * Resolves the language to use, prioritizing the explicitly requested language
     * over the Accept-Language header, and falling back to the default language.
     *
     * @param requestedLanguage explicit language code requested by the client, if any.
     * @param acceptLanguage value of the HTTP Accept-Language header, if present.
     * @return the resolved language code ("en" or "es").
     */
    private fun resolveLanguage(requestedLanguage: String?, acceptLanguage: String?): String {
        if (!requestedLanguage.isNullOrBlank()) {
            return languageCode(requestedLanguage) ?: DEFAULT_LANGUAGE
        }

        if (!acceptLanguage.isNullOrBlank()) {
            try {
                Locale.LanguageRange.parse(acceptLanguage).forEach { range ->
                    languageCode(range.range)?.let { return it }
                }
            } catch (_: IllegalArgumentException) {
            }
        }

        return DEFAULT_LANGUAGE
    }

    /**
     * Extracts a supported language code from a language tag.
     *
     * @param languageTag a language tag such as "en-US" or "es_ES".
     * @return "en", "es", or null if the language is not supported.
     */
    private fun languageCode(languageTag: String): String? {
        return when (languageTag.substringBefore('-').substringBefore('_').lowercase(Locale.ROOT)) {
            "en" -> "en"
            "es" -> "es"
            else -> null
        }
    }

    /**
     * Determines the part of the day from the given time.
     *
     * @param time the current time.
     * @return "morning", "afternoon", or "night".
     */
    private fun resolveTimeOfDay(time: LocalTime): String {
        return when (time.hour) {
            in 5..11 -> "morning"
            in 12..19 -> "afternoon"
            else -> "night"
        }
    }

    /**
     * Builds the final greeting message for the given language, time of day, and name.
     *
     * @param language the resolved language code.
     * @param timeOfDay the part of the day.
     * @param name the name of the person to greet.
     * @return the formatted greeting message.
     */
    private fun messageFor(language: String, timeOfDay: String, name: String): String {
        val subject = name.takeIf { it.isNotBlank() }?.let { ", $it" }.orEmpty()
        return if (language == "es") {
            "${spanishGreeting(timeOfDay)}$subject!"
        } else {
            "${englishGreeting(timeOfDay)}$subject!"
        }
    }

    /**
     * Returns the English greeting for the given time of day.
     *
     * @param timeOfDay the part of the day.
     * @return the English greeting text.
     */
    private fun englishGreeting(timeOfDay: String): String = when (timeOfDay) {
        "morning" -> "Good morning"
        "afternoon" -> "Good afternoon"
        else -> "Good night"
    }

    /**
     * Returns the Spanish greeting for the given time of day.
     *
     * @param timeOfDay the part of the day.
     * @return the Spanish greeting text.
     */
    private fun spanishGreeting(timeOfDay: String): String = when (timeOfDay) {
        "morning" -> "Buenos días"
        "afternoon" -> "Buenas tardes"
        else -> "Buenas noches"
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "en"
    }
}
