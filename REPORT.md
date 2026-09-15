# Lab 1 Git Race -- Project Report

This note uses the same disclosure fields as the group-project **AI use (10%)** slice. Lab 1 is still **limited**: assistive GenAI only — not a full or substantial generated solution. The project will later expect agents plus `AGENTS.md` and one skill; you do **not** need those here.

Do not invent a percentage of “AI vs original” lines. Empty or fake disclosure fails this lab.

## What I specified

I decided to add two related functionalities to the greeting logic.

In the first place, I specified server-side language selection. A client can send a `lang` query
parameter or an `Accept-Language` in the HTTP query header. The `lang` parameter must have priority over
the header. The same server-generated greeting must be visible in the HTML page and in
the JSON response from `/api/hello`.

Second, I specified a time-dependent greeting. The server must return *Good morning*,
*Good afternoon* or *Good evening* in English, and the equivalent Spanish greeting,
according to the time of the request. The time ranges are defined by the server clock.

I would know that the increment works when requests with different times and language
preferences produce the expected message, language, and time-of-day values, and when
the unit, MVC, and integration tests pass.

## What I changed

To add the new functionality I changed the controller file, added a service file with the new logic and modified the test scripts.

- **HelloController.kt**: the page controller (HelloController) now reads `name`, `lang`, and
    `Accept-Language`, and sends the result from the service to Thymeleaf. The API
    controller (HelloApiController) uses the same service and returns `message`, `language`, `timeOfDay`,
    and the existing `timestamp` fields.
- **GreetingService.kt**: added the shared server-side greeting logic. It supports
    English and Spanish, selecting the language specified in the `lang` attribute or the first supported language from `Accept-Language`. It falls back to English when neither of them exist or for an unsupported or malformed value.
- **welcome.html**: the document language is now set with the language selected by
    the server, and the main message continues to come from the model.
- **HelloControllerMVCTests.kt**: added web-layer checks for query language,
    header language, precedence, and the JSON response.
- **HelloControllerUnitTests.kt**: adapted controller tests to the service and its
    new request arguments.
- **GreetingServiceTests.kt**: added deterministic tests for morning, afternoon,
    night, language precedence, header fallback and unsupported languages.
- **IntegrationTest.kt**: updated end-to-end checks for the new server and JSON behaviour.

## Technical decisions

- Keep the same API endpoints but adding the necessary parameters for the new functionality.
- The hours considered are:

                5..11 -> morning
                12..19 -> afternoon
                rest -> night

- Prioritize the `lang` query parameter above the `Accept-Language` HTTP header. This is because the `lang` parameter is used explicitly in a query or app, whereas the `Accept-Language` refers to the configuration preferred by the browser/user.
- The two supported languages are English and Spanish.
- Use a shared `GreetingService` so that the HTML and JSON endpoints cannot implement
    different greeting rules.
- Inject a `Clock` into the service. Production uses the system clock, while tests use
    a fixed clock and therefore do not depend on the time when the tests run.
- Keep the existing endpoint paths and the JSON `timestamp` field for compatibility.
- I rejected client-side translation because the requirement is that the server decides
    the greeting.
- Create a separate test for the new service added.
- Modify the existing tests to cover possible situations and adapt them to the new code.

## How I verified

I ran the following commands:

```text
./gradlew test
./gradlew check
```

After the controller change, the first test run failed during test compilation because the old tests still called the previous constructors and method signatures. 

I updated those tests to construct `GreetingService` with a fixed `Clock` and to pass the new
language arguments. I also removed a time-specific MVC assertion so that it checks the
response structure without depending on the real machine time.

After these changes, `./gradlew test` completed successfully. With the `./gradlew check` command I made sure that the project configuration was correct and that there were no compilation problems.

I also tested the new functionality trough the web page, making sure that the greeting complied with the time and language restriction (via both `Accept-Language` and `lang` parameter). To access the web page, it is necessary to run this command:

```bash
./gradlew bootRun
```

Examples of URLs used to test the functionality:

http://127.0.0.1:8080/api/hello?name=Berta&lang=en :
Provides a JSON time-dependent response in English and personalized to Berta.

http://127.0.0.1:8080/api/hello :
Provides a JSON time-dependent response in the language configured in the browser and personalized to World (default name).

http://127.0.0.1:8080/?name=Berta&lang=es : Provides a greeting in the main web page in Spanish and personalized to Berta.

## AI disclosure

- **Tools / skills:** GitHub Copilot in VS Code and DeepSeek-R1. 
- **Purpose:**
    - Help outline the backend change before implementation.
    - Help identify the controller, template and existing tests affected by the server-side greeting requirement.
    - Suggest and assist the implemention of test cases for time and language selection.
    - Assistance for documenting the code.
- **Representative prompts:**
    - "Make a plan for a server-decided greeting based on time of day and
        Accept-Language/lang, shown in both the page and JSON."
    - "Add KDoc documentation to this `.kt` file."
- **Affected files/sections:** the implementation assistance affected
    `src/main/kotlin/service/GreetingService.kt`,
    `src/main/kotlin/controller/HelloController.kt` and integration tests. Documentation assistance affected also the test files.

- **Validation steps:** I reviewed the generated code changes and ran `./gradlew test`. As it failed the first time, I fixed the old test calls
    for no compilation failure and ran the test command again, successfully. I also
    ran the project checks before submission and reviewed the final diff.
- **Citations:** none. No external snippets were copied or adapted.
- **Human-reviewed:** I reviewed every line of code that was generated with AI. I chose the new functionality, API structure, supported languages, time ranges and injected `Clock`. I checked the controller signatures, the
    response fields, the template language attribute and the test assertions. I
    rejected client-only greeting logic.
