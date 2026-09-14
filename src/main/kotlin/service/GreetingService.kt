package es.unizar.webeng.hello.service

import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalTime
import java.util.Locale

data class Greeting(
    val message: String,
    val language: String,
    val timeOfDay: String
)

@Service
class GreetingService(
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun greet(name: String, requestedLanguage: String?, acceptLanguage: String?): Greeting {
        val language = resolveLanguage(requestedLanguage, acceptLanguage)
        val timeOfDay = resolveTimeOfDay(LocalTime.now(clock))
        val message = messageFor(language, timeOfDay, name)
        return Greeting(message, language, timeOfDay)
    }

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

    private fun languageCode(languageTag: String): String? {
        return when (languageTag.substringBefore('-').substringBefore('_').lowercase(Locale.ROOT)) {
            "en" -> "en"
            "es" -> "es"
            else -> null
        }
    }

    private fun resolveTimeOfDay(time: LocalTime): String {
        return when (time.hour) {
            in 5..11 -> "morning"
            in 12..19 -> "afternoon"
            else -> "night"
        }
    }

    private fun messageFor(language: String, timeOfDay: String, name: String): String {
        val subject = name.takeIf { it.isNotBlank() }?.let { ", $it" }.orEmpty()
        return if (language == "es") {
            "${spanishGreeting(timeOfDay)}$subject!"
        } else {
            "${englishGreeting(timeOfDay)}$subject!"
        }
    }

    private fun englishGreeting(timeOfDay: String): String = when (timeOfDay) {
        "morning" -> "Good morning"
        "afternoon" -> "Good afternoon"
        else -> "Good evening"
    }

    private fun spanishGreeting(timeOfDay: String): String = when (timeOfDay) {
        "morning" -> "Buenos días"
        "afternoon" -> "Buenas tardes"
        else -> "Buenas noches"
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "en"
    }
}
