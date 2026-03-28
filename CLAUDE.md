# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew bootRun                  # Run the application
./gradlew build                    # Build
./gradlew test                     # Run all tests
./gradlew test --tests="ClassName" # Run a single test class
./gradlew test --tests="ClassName.methodName" # Run a single test method
./gradlew ktlintCheck              # Lint
./gradlew ktlintFormat             # Auto-format
```

## Architecture

The project uses **Hexagonal / Clean Architecture** with a single bounded context package today (`management`), with a `scrum` BC planned. All application code lives under `src/main/kotlin/at/fhtw/openscrum/`.

Each bounded context is structured in four layers:

- **domain** — aggregates, value objects, repository interfaces, domain services. No framework dependencies.
- **application** — use case services (e.g. `UserApplicationService`), DTOs, CQRS-style commands (e.g. `RegisterUserCommand`).
- **infrastructure** — JPA entities + repositories, Spring Security integration, bean configuration. Adapters that implement domain interfaces.
- **presentation** — Thymeleaf controllers. HTMX fragment endpoints are annotated with `@HxRequest`.

### Domain model conventions
Value objects are used for identity and validation: `UserId` (UUID wrapper), `EmailAddress`, `FullName`. The `Role` enum has values `USER` and `MANAGER`. The JPA entity (`UserEntity`) is separate from the domain model (`User`); conversion happens in `JpaUserRepository`.

### HTMX + Thymeleaf
Pages use `hx-get`/`hx-post` attributes for dynamic interactions without full page reloads. Fragment endpoints return partial HTML and are annotated with `@HxRequest`. The logout handler writes an `HX-Redirect` header instead of a standard redirect so HTMX handles navigation correctly.

### Security
Spring Security with form login. Roles are prefixed `ROLE_` internally. Only `ROLE_MANAGER` can access `/users/register`. HTMX requests that hit an unauthenticated endpoint receive a redirect header via `HxRefreshHeaderAuthenticationEntryPoint`.

### Database
- **Runtime:** PostgreSQL (driver in classpath; datasource must be provided via env/config)
- **Tests:** H2 for unit tests; TestContainers (PostgreSQL 15.3) for integration/E2E tests activated via `@ActiveProfiles("postgres")`
- DDL auto is set to `update`

### Testing layers
- **Unit tests** — Mockito Kotlin mocks, no Spring context (domain + application layer)
- **Integration tests** — `@SpringBootTest` + TestContainers for repository tests
- **E2E tests** — Selenium with headless Chrome, `@SpringBootTest(webEnvironment = DEFINED_PORT)` + `@ActiveProfiles("postgres")`

Tests follow Given/When/Then comment style and use AssertJ assertions. Each test class resets state in `@AfterEach`.

## Domain

The ubiquitous language is documented in `src/docs/ubiquitous-language.md`. There are two bounded contexts:

- **Management** — user administration (register, update, delete, promote to manager), project lifecycle, team role assignments, product backlog management. Roles: `Manager`, `User`.
- **Scrum** — full sprint lifecycle (planning, daily, review, retrospective, increment). Roles: `Product Owner`, `Scrum Master`, `Developer`.

"Project" means different things in each context: a company project in Management, a Scrum-executed project in Scrum.

User stories are tracked in `src/docs/user-stories.md`.

## Key dependencies

| Library | Version | Purpose |
|---|---|---|
| Spring Boot | 4.0.4 | Framework |
| Kotlin | 2.3.20 | Language |
| Spring Modulith | 2.0.4 | Modular architecture support |
| htmx.org (WebJar) | 2.0.8 | Frontend interactivity |
| htmx-spring-boot-thymeleaf | 5.1.0 | `@HxRequest` and HTMX helpers |
| Selenium | 4.41.0 | E2E tests |
| TestContainers | 2.0.4 | Integration test databases |
| Log4j2 | — | Logging (Logback excluded); app logs at TRACE level |
