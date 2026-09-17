# Ledger — Student Course & Grade Management System

A full-stack, data-driven Java application built with **Spring Boot**, **MySQL** (with an H2
zero-setup fallback), a **RESTful API**, and a lightweight **HTML/JS dashboard**. Built to satisfy
a coursework brief requiring: backend + CRUD, database interaction, an API/GUI, robust error
handling, and complete documentation.

> 🎥 **Video demo:** ADD_YOUR_YOUTUBE_OR_ONEDRIVE_LINK_HERE
> 📄 **Project report:** see `/docs/Project_Report.docx`

---

## 1. What it does

Tracks students, the courses they can take, and their enrollment/grade in each course per
semester, and computes each student's GPA automatically from their graded enrollments.

- **Student** — registration number, name, email, major, DOB, enrollment date, computed GPA
- **Course** — code, title, credit hours, department, description
- **Enrollment** — links a Student + Course + semester, carries a letter Grade

## 2. Architecture

```
Browser (index.html/app.js)
        │  fetch() → JSON
        ▼
Controller layer   (REST endpoints, HTTP concerns only)
        ▼
Service layer      (business rules: uniqueness checks, GPA calc, grade parsing)
        ▼
Repository layer   (Spring Data JPA — CRUD + custom queries)
        ▼
Database           (MySQL in production, H2 in-memory for the "demo" profile)
```

Errors thrown at any layer bubble up to a single `GlobalExceptionHandler`
(`@RestControllerAdvice`), which converts them into a consistent JSON error shape and an
appropriate HTTP status — the API never returns a raw stack trace or crashes on bad input.

## 3. Tech stack

| Layer          | Technology                                   |
|----------------|-----------------------------------------------|
| Language       | Java 25                                        |
| Framework      | Spring Boot 3.3 (Web, Data JPA, Validation)    |
| Database       | MySQL 8 (primary) / H2 (demo/dev profile)      |
| ORM            | Hibernate (via Spring Data JPA)                |
| API style      | REST, JSON                                     |
| Frontend       | Plain HTML5 / CSS3 / vanilla JS (no build step)|
| Build tool     | Maven                                          |

## 4. Running the project

### Option A — MySQL (default profile)

1. Install MySQL and make sure it's running locally.
2. Create the database (or let the app do it — the JDBC URL uses
   `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE sms_db;
   ```
3. Edit `src/main/resources/application-mysql.properties` with your MySQL username/password.
4. Run:
   ```bash
   mvn spring-boot:run
   ```
5. Open **http://localhost:8080** for the dashboard.

### Option B — zero setup (H2 in-memory demo profile)

No MySQL install needed — useful for quickly trying the app or grading:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```
Data resets each time you stop the app. The H2 console (to inspect tables/data visually) is at
**http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:smsdb`, user `sa`, no password).

### Running tests
```bash
mvn test
```

## 5. REST API reference

Base URL: `http://localhost:8080/api`

| Method | Endpoint                          | Description                              |
|--------|------------------------------------|-------------------------------------------|
| GET    | `/health`                          | Health check                              |
| POST   | `/students`                        | Create a student                          |
| GET    | `/students`                        | List all students (with computed GPA)     |
| GET    | `/students/{id}`                   | Get one student                           |
| PUT    | `/students/{id}`                   | Update a student                          |
| DELETE | `/students/{id}`                   | Delete a student (cascades enrollments)   |
| GET    | `/students/{id}/gpa`                | Get just the computed GPA                 |
| POST   | `/courses`                         | Create a course                           |
| GET    | `/courses`                         | List all courses                          |
| GET    | `/courses/{id}`                    | Get one course                            |
| PUT    | `/courses/{id}`                    | Update a course                           |
| DELETE | `/courses/{id}`                    | Delete a course (cascades enrollments)    |
| POST   | `/enrollments`                     | Enroll a student in a course/semester     |
| GET    | `/enrollments`                     | List all enrollments                      |
| GET    | `/enrollments/{id}`                | Get one enrollment                        |
| GET    | `/enrollments/student/{studentId}` | List a student's enrollments               |
| GET    | `/enrollments/course/{courseId}`   | List a course's enrollments                |
| PATCH  | `/enrollments/{id}/grade`          | Update just the grade                     |
| DELETE | `/enrollments/{id}`                | Remove an enrollment                      |

Example request:
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"registrationNumber":"REG-2026-004","firstName":"Sara","lastName":"Kim","email":"sara.kim@campus.edu","major":"CS"}'
```

Example error response (validation failure):
```json
{
  "timestamp": "2026-08-26 10:12:03",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/students",
  "details": ["email: Email must be a valid email address"]
}
```

## 6. Project structure
```
src/main/java/com/university/sms/
  ├── SmsApplication.java        # entry point
  ├── model/                     # JPA entities (Student, Course, Enrollment, Grade)
  ├── repository/                # Spring Data JPA repositories
  ├── service/ , service/impl/   # business logic
  ├── controller/                # REST controllers
  ├── dto/                       # API request/response objects + ErrorResponse
  ├── exception/                 # custom exceptions + GlobalExceptionHandler
  └── config/                    # CORS config
src/main/resources/
  ├── application*.properties    # MySQL / demo(H2) profiles
  └── static/                    # HTML/CSS/JS dashboard
sql/schema_reference.sql         # documentation-only reference schema + seed data
docs/Project_Report.docx         # full written report
```

## 7. Author

- **Name:** ADD_YOUR_NAME
- **Student ID:** ADD_YOUR_STUDENT_ID
- **Repository:** ADD_YOUR_GITHUB_REPO_URL
- **Video demo:** ADD_YOUR_VIDEO_URL
