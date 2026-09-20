# Lab 1 Git Race -- Project Report

## 1. What I specified

### Objective
I decided to add two related functionalities to the greeting logic.

In the first place, I specified server-side language selection. A client can send a `lang` query parameter or an `Accept-Language` in the HTTP query header. The `lang` parameter must have priority over the header. The same server-generated greeting must be visible in the HTML page and in the JSON response from `/api/hello`.

Second, I specified a time-dependent greeting. The server must return *Good morning*, *Good afternoon* or *Good evening* in English, and the equivalent Spanish greeting, according to the time of the request. The time ranges are defined by the server clock.

I would know that the increment works when requests with different times and language preferences produce the expected message, language, and time-of-day values, and when the unit, MVC, and integration tests pass.

### Bonus
I specified the implementation of a traffic control system (Rate Limiting) to restrict the number of requests a user can make within a certain timeframe, returning an HTTP 429 (Too Many Requests) error when exceeded. This was developed in three progressive phases using feature branches:

1. **Manual Implementation:** Building a custom Servlet filter to manually handle request interception, timestamps, and concurrency.
2. **Specialized Library:** Refactoring the solution to use the Bucket4j library, implementing a Token Bucket algorithm.
3. **Framework Integration:** Delegating the traffic control entirely to the infrastructure layer using Spring Cloud Gateway Server WebMVC.

I set the limit to 10 requests maximum per minute per IP.

I would know that the bonus works when it only allows 10 requests per minute to the server from the same machine and when the filter tests pass.

## 2. What I changed

### Objective
To add the new functionality I changed the controller file, added a service file with the new logic and modified the test scripts, in the *main* branch.

- **HelloController.kt**: the page controller (HelloController) now reads `name`, `lang`, and the `Accept-Language` header, and sends the result from the service to Thymeleaf. The API controller (HelloApiController) uses the same service and returns `message`, `language`, `timeOfDay`, and the existing `timestamp` fields.
- **GreetingService.kt**: added the shared server-side greeting logic. It supports English and Spanish, selecting the language specified in the `lang` attribute or the first supported language from `Accept-Language`. It falls back to English when neither of them exist or for an unsupported or malformed value.
- **welcome.html**: the document language is now set with the language selected by the server and the main message continues to come from the model.
- **HelloControllerMVCTests.kt**: added web-layer checks for query language, header language, precedence and the JSON response.
- **HelloControllerUnitTests.kt**: adapted controller tests to the service and its new request arguments.
- **GreetingServiceTests.kt**: added deterministic tests for morning, afternoon, night, language precedence, header fallback and unsupported languages.
- **IntegrationTest.kt**: updated end-to-end checks for the new server and JSON behavior.

### Bonus

I made the additions in three different branches.

- ***feature/traffic_control* branch:** Added **RateLimitFilter.kt** mapped globally to intercept all routes. Removed a 2-second periodic polling function from **http-debug.js** to avoid false positives in the filter. Updated **ci.yml** to trigger continuous integration on all `feature/*` branches. Added **RateLimitFilterTest.kt** and modified MVC and integration tests to exclude the filter.
- ***feature/traffic_control_bucket4j* branch:** Modified **RateLimitFilter.kt** to replace manual timestamp maps with Bucket4j buckets.
- ***feature/traffic_control_gateway* branch:** Removed **RateLimitFilter.kt** entirely. Added **RateLimiterConfig.kt** to configure Spring Boot, Spring Cloud Gateway, and Caffeine. Refactored integration, MVC and filter tests to support the new framework routing.

## 3. Technical decisions

### Objective

- Keep the same API endpoints but adding the necessary parameters for the new functionality.
- The hours considered are:

                5..11 -> morning
                12..19 -> afternoon
                rest -> night

- Prioritize the `lang` query parameter above the `Accept-Language` HTTP header. This is because the `lang` parameter is used explicitly in a query or app, whereas the `Accept-Language` refers to the configuration preferred by the browser/user.
- The two supported languages are English and Spanish.
- Use a shared `GreetingService` so that the HTML and JSON endpoints cannot implement different greeting rules.
- Inject a `Clock` into the service. Production uses the system clock, while tests use a fixed clock and therefore do not depend on the time when the tests run.
- Keep the existing endpoint paths and the JSON `timestamp` field for compatibility.
- I rejected client-side translation because the requirement is that the server decides the greeting.
- Create a separate test for the new service added.
- Modify the existing tests to cover possible situations and adapt them to the new code.

### Bonus

- **Concurrency Control:** For the manual implementation, `ConcurrentHashMap` and `CopyOnWriteArrayList` were chosen to ensure thread safety when multiple IPs access the server simultaneously.
- **Hard Request Cutoff:** If the limit (10 requests) is exceeded, the application writes a 429 status code directly to the `HttpServletResponse` and halts execution without calling `chain.doFilter()`, preventing the request from reaching the controllers.
- **Excluding Observability:** I decided to exclude `/actuator/health` from the rate limiter. In a professional architecture, health checks and static assets should not be subject to business quotas to avoid false positives and monitoring drops.
- **Client Polling Removal:** I decided to remove the 2-second interval check for HTML changes in `http-debug.js`, as it was artificially consuming the rate limit quota.
- **Test Isolation via Conditional Properties:** Because the filter's request counter was affecting the MVC and integration tests, I configured the filter to be optionally disabled via a property during those specific test suites.

## 4. How I verified

### Objective
I ran the following command:

```text
./gradlew check
```

After the controller change, the first test run failed during test compilation because the old tests still called the previous constructors and method signatures. I updated those tests to construct `GreetingService` with a fixed `Clock` and to pass the new language arguments. I also removed a time-specific MVC assertion so that it checks the response structure without depending on the real machine time. In addition, I fixed an issue in the MVC tests where the Spanish assertion expected specifically "Buenas" by changing it to expect "Buen" to handle both "Buenos" and "Buenas" regardless of the time of day.

After these changes, `./gradlew check` completed successfully. With this command I made sure that the project configuration was correct, that there were no compilation problems and that all the tests passed.

I also tested the new functionality through the web page, making sure that the greeting complied with the time and language restriction (via both `Accept-Language` and `lang` parameter). To access the web page, it is necessary to run this command:

```text
./gradlew bootRun
```

Examples of URLs used to test the functionality:

- http://127.0.0.1:8080/api/hello?name=Berta&lang=en: Provides a JSON time-dependent response in English and personalized to Berta.
- http://127.0.0.1:8080/api/hello: Provides a JSON time-dependent response in the language configured in the browser and personalized to World (default name).
- http://127.0.0.1:8080/?name=Berta&lang=es: Provides a greeting in the main web page in Spanish and personalized to Berta.

### Bonus
I manually tested the 429 Too Many Requests response by triggering rapid requests in the browser, verifying the filter correctly limited the requests to 10 per minute.

I also used the rate filter tests created to explicitly verify the threshold limits and exclusion rules, and to include them in CI.

I resolved compilation and testing errors during the Gateway integration phase caused by Spring Cloud dependency issues and random port assignments mismatching the 8080 configuration used by the rate limiter.

## 5. AI disclosure
- **Tools / skills:** GitHub Copilot in VS Code, DeepSeek-R1 and Gemini 2.5 Flash.

- **Purpose:**

    - Help outline the backend changes before implementation.
    - Help identify the controller, template and existing tests affected by the server-side greeting requirement.
    - Suggest and assist the implementation of test cases for time and language selection.
    - Assistance for documenting the code and the report.

- **Representative prompts:**

    - "Make a plan for a server-decided greeting based on time of day and Accept-Language/lang, shown in both the page and JSON."

    - "Add KDoc documentation to this .kt file."

- **Affected files/sections:** the implementation and documentation assistance affected `src/main/kotlin/service/GreetingService.kt`, `src/main/kotlin/controller/HelloController.kt`, test files, `build.gradle.kts`, `RateLimiterConfig.kt` and `REPORT.md`.

- **Validation steps:** I reviewed the generated code changes and ran `./gradlew check`. As it failed the first time, I fixed the old test calls for no compilation failure and ran the test command again, successfully. For the bonus part, I had to fix compilation and dependencies errors for a successful execution. I also ran the project checks before submission and reviewed the final diff.

*   **Citations:** The following resources were used as reference and adapted for the different phases of the bonus implementation:
    *   Baeldung tutorial for implementing a Servlet Filter: https://www.baeldung.com/spring-boot-add-filter
    *   RFC 6585 for the HTTP 429 (Too Many Requests) status code: https://www.rfc-editor.org/info/rfc6585/#section-7.2
    *   Bucket4j documentation for the REST API rate limiting adaptation: https://bucket4j.com/8.19.0/toc.html#limiting-the-rate-of-access-to-rest-api
    *   Spring Cloud Gateway Server WebMVC documentation for the rate limiter filter: https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webmvc/filters/ratelimiter.html
- **Human-reviewed:** I reviewed every line of code that was generated with AI. I chose the new functionality, API structure, supported languages, time ranges, injected `Clock` and rate limit filter functionality. I checked the controller signatures, the response fields, the template language attribute, the test assertions and the filter performance. I rejected client-only greeting logic.
