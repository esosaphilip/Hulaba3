# Contributing to Hulaba Android App

Thanks for your interest in contributing! This document outlines the recommended workflow and standards for contributions.

## Getting Set Up
1. Fork the repository and clone your fork
2. Create a feature branch
   - Example: `git checkout -b feature/attach-pdf-ui-improvements`
3. Use Android Studio to develop and run the app
4. Build with Gradle to ensure changes compile:
   - `./gradlew :app:assembleDebug`

## Development Guidelines
- Kotlin + Jetpack Compose best practices
  - Use immutable state where possible, and expose StateFlow from ViewModels
  - Keep composables small and focused; remember(state) + derivedStateOf when needed
  - Avoid duplicate or unused imports; keep files lint‑clean
- Data and repository layer
  - Prefer suspending functions and DAO queries with Room
  - Use repositories for data orchestration (e.g., StudyMaterialRepository for PDFs)
- Quiz generation
  - `QuizGenerationService` should read text via `PdfTextExtractor` and persist results via repositories
  - Keep long‑running work off the main thread using coroutines

## Testing & Build Checks
- Ensure the project builds: `./gradlew :app:assembleDebug`
- Manual test PDF selection and quiz generation flow before opening a PR
- If you modify the ANR‑related code paths, please run on emulator and a physical device if possible

## Commit Message Conventions
We recommend Conventional Commits for clarity:

- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation updates (README, CONTRIBUTING, etc.)
- `refactor:` code refactoring without behavior change
- `perf:` performance improvements
- `chore:` tooling, CI, build, dependencies

Examples:
- `feat(add-topic): attach PDF via SAF and persist StudyMaterial`
- `fix(viewmodel): remove conflicting imports in TopicViewModel`
- `docs(android): add README with setup and quiz flow`

## Pull Request Checklist
- [ ] Code builds locally with `./gradlew :app:assembleDebug`
- [ ] Basic manual test of PDF attach and quiz generation
- [ ] No obvious lint warnings in changed files
- [ ] Clear PR description summarizing changes and testing steps
- [ ] Screenshots added if UI changes are made

## Reviews
- Be receptive to feedback and iterate quickly
- Keep PRs small and focused for easier review

## Questions
Open an issue or start a discussion in the repository if you need guidance.