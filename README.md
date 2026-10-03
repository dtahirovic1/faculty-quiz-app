# Faculty Quizzes Mobile Application

Android application for university courses and quizzes, developed as part of the **Mobile Application Development** course at the Faculty of Electrical Engineering, University of Sarajevo.

The application allows students to browse quizzes, enroll in courses and groups, complete quizzes, submit answers, and view results. It communicates with a provided REST backend and follows the MVVM architecture.

## Features

- Browse and filter available quizzes
- Enroll in courses and groups
- View quizzes for enrolled courses
- Complete quizzes and submit answers
- View quiz results
- Persist and manage application state
- Retrieve and submit data through a REST API
- Handle network and backend errors

## Technologies

- Kotlin
- Jetpack Compose
- MVVM
- ViewModel
- Kotlin Coroutines
- REST API
- Retrofit
- Room
- StateFlow
- Navigation Compose

## Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture to separate the user interface from application and data-handling logic.

Jetpack Compose is used for the UI, while ViewModels manage UI state and application logic. Kotlin Coroutines are used for asynchronous operations, Retrofit handles communication with the REST backend, and Room provides local data persistence.

## Backend Integration

The application communicates with a provided REST API for operations involving:

- Courses and groups
- Student enrollments
- Quizzes and questions
- Quiz attempts
- Answer submission and results

**Dino Tahirović**  
Faculty of Electrical Engineering, University of Sarajevo
