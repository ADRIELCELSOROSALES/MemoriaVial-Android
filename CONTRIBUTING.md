# Contributing to Capa Vial Predictiva (CVP)

Thank you for taking the time to contribute. This document defines the standards and workflow expected for all contributions to the CVP Android application.

---

## Table of Contents

1. [Code Style](#code-style)
2. [Naming Conventions](#naming-conventions)
3. [Architecture Constraints](#architecture-constraints)
4. [Branch Naming](#branch-naming)
5. [Commit Format](#commit-format)
6. [Pull Request Requirements](#pull-request-requirements)
7. [Design System](#design-system)
8. [Running Tests](#running-tests)

---

## Code Style

CVP follows the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) published by JetBrains. All code must be formatted with `ktlint` before submission — the CI pipeline enforces this automatically.

Key points:

- Indentation: 4 spaces (no tabs).
- Maximum line length: 120 characters.
- Trailing commas are encouraged in multi-line expressions.
- Use expression bodies for simple single-expression functions.
- Prefer `val` over `var` everywhere it is semantically correct.
- Avoid platform types — always annotate nullability explicitly when interoperating with Java.
- Use named arguments for functions with more than two parameters of the same type.

---

## Naming Conventions

| Element | Convention | Example |
|---|---|---|
| Classes and interfaces | PascalCase | `RiskZoneRepository`, `MapViewModel` |
| Objects and companion objects | PascalCase | `CvpTheme`, `AppModule` |
| Functions and properties | camelCase | `fetchRiskZones()`, `alertRadiusMeters` |
| Local variables | camelCase | `filteredZones`, `isLoading` |
| Constants (`const val`) | SCREAMING_SNAKE_CASE | `DEFAULT_ALERT_RADIUS_M` |
| Kotlin files | PascalCase matching the primary class | `MapViewModel.kt`, `RiskZone.kt` |
| Resource files (XML, drawables) | snake_case | `ic_map_pin.xml`, `bg_splash.xml` |
| Packages | lowercase, dot-separated | `com.cvp.app.presentation.map` |

> **Note:** Kotlin source files use PascalCase. Snake_case is reserved for Android resource files only.

---

## Architecture Constraints

CVP follows a strict layered architecture (data -> domain -> presentation). Violating these constraints will result in a PR rejection.

### Composables

- Composables are **pure UI**. They render state and forward events — nothing more.
- No business logic, no direct repository access, no coroutine launches (except `LaunchedEffect` for one-shot side effects driven by state).
- Composables receive `UiState` data classes and lambda callbacks as parameters. They do not hold or compute state themselves beyond simple local UI state (e.g., expanded/collapsed toggles).

### ViewModels

- ViewModels own and expose all screen state via `StateFlow<UiState>`.
- Use `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)` for flows exposed to the UI.
- ViewModels call use cases or repositories — they do not contain SQL queries, file I/O, or network calls directly.
- One ViewModel per screen route. Do not share a single ViewModel across unrelated screens.

### Repositories

- Repositories are the **single source of truth** for all data access (local DataStore, bundled GeoJSON assets, future network endpoints).
- All repository methods are `suspend` functions or return `Flow`.
- The domain layer depends on repository **interfaces**, not implementations. Implementations live in the data layer and are injected via Koin.

### Dependency Injection

- CVP uses Koin. Do not introduce Hilt, Dagger, or manual service locators.
- All modules are declared under `di/`. Add new bindings there; do not call `get()` or `inject()` outside of Composables/ViewModels at the entry points.

---

## Branch Naming

Branches must follow one of these prefixes:

| Prefix | Use |
|---|---|
| `feature/*` | New functionality |
| `fix/*` | Bug fixes |
| `chore/*` | Build, dependency, tooling, CI changes |
| `docs/*` | Documentation only |
| `refactor/*` | Internal restructuring without behaviour change |
| `test/*` | Adding or fixing tests |

Examples: `feature/proximity-alerts`, `fix/zone-cluster-crash`, `chore/update-mapbox-11`.

Branch names must be lowercase and use hyphens as word separators.

---

## Commit Format

CVP uses [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).

```
<type>(<optional scope>): <short imperative description>

[optional body]

[optional footer(s)]
```

Allowed types:

| Type | When to use |
|---|---|
| `feat` | A new feature visible to the user |
| `fix` | A bug fix |
| `chore` | Build scripts, dependency bumps, project config |
| `docs` | Documentation changes only |
| `test` | Adding or modifying tests without changing production code |
| `refactor` | Code restructuring that neither fixes a bug nor adds a feature |
| `perf` | Performance improvements |
| `style` | Formatting, whitespace (no logic change) |

Examples:

```
feat(map): add cluster expansion animation for risk zones
fix(alerts): prevent duplicate proximity notification on screen rotation
chore(deps): bump Mapbox to 11.9.0
test(domain): add unit tests for ProximityAlertUseCase
```

- Use the imperative mood in the description: "add", not "added" or "adds".
- Keep the subject line under 72 characters.
- Reference issues in the footer: `Closes #42`.

---

## Pull Request Requirements

### Before opening a PR

- The branch is rebased on the latest `main`.
- All existing tests pass locally (see [Running Tests](#running-tests)).
- New domain logic (use cases, repository implementations, domain models) has unit tests covering the happy path and at least one error/edge case.
- No new lint warnings are introduced (`./gradlew lint`).
- No hardcoded colors, dimensions, or strings in Composables (see [Design System](#design-system)).

### Public API changes

- Any change to a public interface, sealed class hierarchy, or navigation route definition is a **breaking change** and must be discussed in an issue before implementation.
- Deprecate before removing. Add `@Deprecated` with a `ReplaceWith` expression and allow at least one release cycle before deletion.

### PR description

Provide a clear description covering:

1. What changed and why.
2. Screenshots or screen recordings for any UI change.
3. Testing steps for the reviewer.

---

## Design System

All UI code must use the CVP design system. Hardcoding visual values is not allowed.

| Need | Correct approach |
|---|---|
| Colors | `CvpTheme.colors.*` (e.g., `CvpTheme.colors.severityHigh`) |
| Spacing | `CvpTheme.spacing.*` (e.g., `CvpTheme.spacing.md`) |
| Shapes | `CvpTheme.shapes.*` or M3 `MaterialTheme.shapes.*` |
| Typography | `MaterialTheme.typography.*` (Inter font, wired in `CvpTheme`) |
| Components | Use existing CVP components (`CvpButton`, `CvpCard`, `CvpChip`, `CvpTopBar`, etc.) before creating new ones |

If a new color, spacing value, or component is genuinely required, add it to the design system in a dedicated commit rather than inlining it at the call site.

---

## Running Tests

**Unit tests** (JVM, no emulator required):

```bash
./gradlew test
```

**Instrumentation tests** (requires a connected device or emulator):

```bash
./gradlew connectedAndroidTest
```

**Lint check:**

```bash
./gradlew lint
```

**All checks together (recommended before pushing):**

```bash
./gradlew test lint
```

Test source sets follow the standard Android layout:

- `app/src/test/` — unit tests (ViewModels, use cases, repository logic with fakes)
- `app/src/androidTest/` — instrumentation tests (Compose UI tests, DataStore integration)

Use `kotlinx-coroutines-test` and `turbine` for testing `StateFlow`/`Flow` in ViewModels and repositories.
