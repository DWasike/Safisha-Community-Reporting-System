# Safisha – Community Reporting System

**Safisha** is an Android-based community reporting application developed using **Kotlin and XML** that enables residents to report environmental and infrastructure problems within their communities and allows administrators to manage and track the resolution of submitted reports.

The application aims to strengthen communication between residents and responsible authorities by providing a centralized platform for reporting issues, monitoring their progress, and promoting accountability in community problem-solving.

## Problem Statement

Environmental and infrastructure challenges such as potholes, faulty streetlights, poor waste management, blocked drainage systems, sewage problems, and damaged public facilities often go unreported or lack an effective mechanism for tracking their resolution.

Safisha seeks to address this challenge by providing a structured and accessible reporting system through which residents can submit complaints and administrators can monitor and manage reported issues.

## Key Features

- **User Authentication:** Registration and login for residents and administrators.
- **Issue Reporting:** Submit reports with an issue category, location, and description.
- **Issue Categorization:** Organize reports into predefined environmental and infrastructure categories.
- **Report Tracking:** View submitted reports and monitor their resolution status.
- **Administrative Management:** Allow administrators to view reports, update their statuses, and manage reported issues.
- **Local Data Persistence:** Store user and issue-report information using Room and SQLite.

## Issue Categories

Safisha supports reporting across the following categories:

- Road Damage and Potholes
- Street Lighting
- Traffic and Parking
- Waste Management
- Drainage
- Sewage
- Water Supply
- Public Facilities
- Illegal Construction
- Noise and Public Nuisance

## Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Application development |
| XML | Android user interface layouts |
| Android Studio | Development environment |
| Room | Local database abstraction |
| SQLite | Local data storage |
| Coroutines and Flow | Asynchronous operations and observable data |
| Git and GitHub | Version control and team collaboration |

## Architecture

The application follows a layered **Model-View-ViewModel (MVVM)** architecture to promote separation of concerns, maintainability, and organized application development.

The main architectural components include:

- **Presentation Layer:** XML layouts and Android UI components.
- **ViewModel Layer:** Manages application state and coordinates user interactions.
- **Repository Layer:** Provides a consistent interface for data operations.
- **Data Layer:** Uses Room and SQLite for local persistence.

## Project Scope

Safisha focuses on community-based environmental and infrastructure reporting, report management, and status tracking. It is designed as a centralized platform that supports communication and accountability between residents and administrators.

The project does not currently include automated issue detection, AI-based classification, GPS-based mapping, or direct integration with municipal reporting systems.

## Project Status

**Under Development**

Safisha is being developed collaboratively as an Android application, with individual team members contributing to database management, application logic, user interfaces, and administrative functionality.

## Contributors

Developed collaboratively by the Safisha project team.
