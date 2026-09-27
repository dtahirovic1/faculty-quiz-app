# Faculty Quizzes Mobile Application

Android mobile application for managing university courses and quizzes, developed as part of the **Mobile Application Development** course at the Faculty of Electrical Engineering, University of Sarajevo.

The project was developed incrementally through four development iterations, starting with a local Jetpack Compose application and evolving into a structured client application connected to a REST backend.

## Features

- Browse available quizzes
- Filter quizzes by status
- Enroll in university courses and groups
- View quizzes associated with enrolled courses
- Dynamic quiz status indicators
- Navigate between filtering and quiz screens
- Start quiz attempts
- View quiz questions
- Submit and update answers
- Display quiz results
- Persist application state during navigation and configuration changes
- Communicate with a REST backend
- Handle backend errors without crashing the application

## Tech Stack

### Android

- Kotlin
- Jetpack Compose
- Navigation Compose
- ViewModel
- Kotlin Coroutines
- Repository Pattern
- REST API communication
- Android Studio

### Backend

The Android application communicates with the provided RMA26 backend service.

The backend exposes endpoints for:

- Courses
- Groups
- Student enrollments
- Quizzes
- Questions
- Quiz attempts
- Answers