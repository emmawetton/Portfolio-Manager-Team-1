# Copilot Instructions for Portfolio-Manager-Team-1

- This is a Spring Boot Java app using Java 17 and Maven. The app bootstraps from `PortfolioManager/src/main/java/com/training/banking/PortfolioManagerApplication.java`.
- Project follows a layered package structure: `controller`, `service`, `repository`, and `model` under `com.training.banking`.
- Current domain placeholders are `PortfolioManager/src/main/java/com/training/banking/model/Asset.java` and `.../Portfolio.java`; both are empty stubs now.
- Tests are Spring context-based in `PortfolioManager/src/test/java/com/training/banking/PortfolioManagerApplicationTests.java`.

## Build and Run
- Use Maven wrapper on Windows: `PortfolioManager\mvnw.cmd clean package` and `PortfolioManager\mvnw.cmd test`.
- Run the app locally with `PortfolioManager\mvnw.cmd spring-boot:run`.
- If the wrapper is unavailable, use installed Maven from repo root: `mvn -f PortfolioManager/pom.xml clean package`.

## What to keep in mind
- No controllers, services, or repositories currently exist in source; add new Spring beans under `src/main/java/com/training/banking/...`.
- `Portfolio.java` already imports `jakarta.persistence.*`, so future persistence should use Jakarta/JPA annotations consistently.
- Keep the bootstrap class `PortfolioManagerApplication` minimal; business logic belongs in the `service` layer.
- Avoid modifying generated `target/` output; source of truth is under `PortfolioManager/src`.

## AI agent behavior
- Prefer changing or adding files under `PortfolioManager/src/main/java/com/training/banking` and test files under `PortfolioManager/src/test/java/com/training/banking`.
- For new features, preserve the existing layered package intent and keep REST/web concerns in `controller`, business logic in `service`, persistence interfaces in `repository`, and domain objects in `model`.
- When adding dependencies, update `PortfolioManager/pom.xml` only if needed for Spring Data, validation, or web endpoints.
- Keep instructions concise and avoid adding generic project practices unless they follow the current Spring Boot/Maven structure.

## Important files
- `PortfolioManager/pom.xml` — Maven configuration and Spring Boot dependency management.
- `PortfolioManager/src/main/java/com/training/banking/PortfolioManagerApplication.java` — application entrypoint.
- `PortfolioManager/src/main/java/com/training/banking/model/Asset.java` and `PortfolioManager/src/main/java/com/training/banking/model/Portfolio.java` — current domain model placeholders.
- `PortfolioManager/src/test/java/com/training/banking/PortfolioManagerApplicationTests.java` — basic application context smoke test.
