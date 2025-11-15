# Hulaba Android App

An Android learning app built with Kotlin, Jetpack Compose, and Room. It helps you create study topics, attach PDF materials, and auto‑generate quizzes from your PDFs using the in‑app quiz generation pipeline.

This README focuses on the Android app located in the `app/` module. A separate experimental web UI (Vite/React) also exists at the repo root, but it is not required to build or run the Android application.

## Key Features
- Add and edit study topics
- Attach a PDF to a topic as a StudyMaterial (primary material)
- Generate quiz questions from the attached PDF
- Take quizzes and see results
- Local persistence via Room (offline‑friendly)

## Tech Stack
- Kotlin, Jetpack Compose (Material3)
- Room (SQLite) for local data
- Coroutines + StateFlow for reactive data
- Android Storage Access Framework for PDF selection

## Project Structure (Android)
```
app/src/main/java/com/example/hulaba3/
├── data/
│   ├── database/            // Room entities & DAOs (Topic, StudyMaterial, Question, Answer, etc.)
│   └── repository/          // Repositories (TopicRepository, StudyMaterialRepository, QuestionRepository, ...)
├── uilayer/                 // Compose screens & components
│   ├── screens/
│   │   ├── MainScreen.kt    // App nav, ViewModel wiring
│   │   └── topicscreens/    // AddTopicScreen, TopicScreen
│   └── components/          // Reusable UI components
├── utils/                   // PdfTextExtractor, GeminiApiService, QuizGenerationService, Notification helpers
└── viewmodel/               // ViewModels (TopicViewModel, QuizViewModel, etc.)
```

## Getting Started
1) Prerequisites
- Android Studio (Giraffe or newer)
- JDK 17 (bundled with AS is fine)
- Android SDK + a device or emulator (API 28+ recommended)

2) Clone and build
```
git clone https://github.com/esosaphilip/Hulaba3.git
cd Hulaba3
./gradlew :app:assembleDebug
```

3) Run
- Open the project in Android Studio
- Select a device/emulator and click Run

## How to Add a Study Topic and Attach a PDF
1) From the Topics list, navigate to “Add Study Note”
2) Enter a topic title
3) Tap “Choose PDF” and select a PDF via the system file picker
   - The app requests and persists read permission for the selected URI
4) Tap “Save Note”
   - The topic is inserted/updated
   - The selected PDF is stored as a `StudyMaterial` and set as primary for that topic

## Quiz Generation Flow
1) `AddTopicScreen` lets you attach a PDF (content URI)
2) `TopicViewModel.attachPdfToTopic()` saves it as `StudyMaterial(fileUrl, fileType=pdf, isPrimary=true)`
3) `QuizGenerationService.generateQuestionsFromPdf()`
   - Looks up the topic’s primary `StudyMaterial`
   - Reads the PDF via `PdfTextExtractor`
   - Sends extracted text to `GeminiApiService` to generate candidate questions
   - Saves generated `Question` + `Answer` rows via `QuestionRepository`

## Data Model Highlights
- Topic: `id`, `title`, `coverImageUrl` (optional), etc.
- StudyMaterial: `topicId`, `title`, `fileUrl` (content URI), `fileType` (pdf), `isPrimary`
- Question / Answer: stored via Room and displayed in quiz screens

## Permissions
- Storage Access Framework is used for picking PDFs (OpenDocument). No broad storage permission is required.
- If you use speaking/audio features elsewhere, RECORD_AUDIO may be requested at runtime.

## Troubleshooting & ANR Diagnostics
If you encounter a “System UI isn’t responding” dialog on emulator:
- Capture events around the ANR:
```
adb logcat -b events -v time | grep -E "am_anr|am_crash|com.android.systemui"
```
- Collect focused logs for System UI and your app:
```
adb logcat --pid $(adb shell pidof -s com.android.systemui) -v time
adb logcat --pid $(adb shell pidof -s com.example.hulaba3) -v time
```
- Emulator sanity pass:
  - Cold boot / Wipe data
  - Update system image
  - Graphics: Software (Swiftshader) or ANGLE
  - Disable snapshots
  - Allocate 2 cores + 4 GB RAM

## Development Notes
- ViewModels are constructed in `MainScreen.kt` using Room DAOs
- `TopicViewModel` exposes `attachPdfToTopic(topicId, pdfUri, title, makePrimary)`
- `QuizViewModel` triggers question generation and loads questions for quizzes

## Building with Gradle (CLI)
```
# Assemble debug APK
./gradlew :app:assembleDebug

# Install on a connected device
./gradlew :app:installDebug
```

## Screenshots
Screenshots are stored at the repo root (e.g., `Screenshot_20250309_151737.png`).

## Contributing
- Use Kotlin formatting and idiomatic coroutines/flows
- Keep imports clean (avoid duplicates and unused)
- Prefer dependency injection via constructor wiring in `MainScreen`
- Write small, composable UI components in `uilayer/components`

## License
This project is for educational purposes. If you plan to distribute, ensure third‑party services and content are used in compliance with their licenses and terms.
