# Lab 1 Git Race

Individual starter for Web Engineering 2026–27. Stack matches the group project: **Java 25 LTS**, **Kotlin 2.4.0**, **Spring Boot 4.1.0**, **Gradle 9.6.0**, **Bootstrap 5.3.8**.

The assignment, AI rules, and deadline are in [`docs/GUIDE.md`](docs/GUIDE.md). Fill [`REPORT.md`](REPORT.md) before you submit.

## Run

Java 25 is required (`./gradlew` uses the wrapper). GitHub Codespaces is optional: fork the starter, then open the Codespace from your fork (`docs/GUIDE.md`).

```bash
git clone https://github.com/UNIZAR-30246-WebEngineering/lab1-git-race.git
cd lab1-git-race
./gradlew check
./gradlew bootRun
```

- UI: <http://localhost:8080>
- JSON: <http://localhost:8080/api/hello>
- Health: <http://localhost:8080/actuator/health>

```bash
./gradlew test
./gradlew test --tests "HelloControllerUnitTests"
```

## Layout

```
src/main/kotlin/HelloWorld.kt
src/main/kotlin/controller/HelloController.kt
src/main/resources/templates/welcome.html
src/test/kotlin/controller/HelloControllerUnitTests.kt
src/test/kotlin/controller/HelloControllerMVCTests.kt
src/test/kotlin/IntegrationTest.kt
```

## License

MIT — see `LICENSE`.
