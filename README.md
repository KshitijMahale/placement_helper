# IntervuLog: Interview Experience Logger and Browser
> A production-ready, full-stack interview experience platform built with Spring Boot and PostgreSQL.

[![UptimeRobot Status](https://img.shields.io/badge/Status-🟢%20Live-9acd32?style=for-the-badge)](https://stats.uptimerobot.com/tvx8PK6R6O/800797106)
![Users](https://img.shields.io/badge/Users-260%2B-blue?style=for-the-badge)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3%2B-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

---

## 💡 Project Overview

**IntervuLog** is a full-stack, college-centric web application designed to centralize and share interview experiences.  
Built with **Spring Boot** and **PostgreSQL**, it enables SPIT students to log and explore real-world interview experiences — helping juniors to prepare better and seniors share their journeys.

> **Currently used by 260+** verified students from Sardar Patel Institute of Technology, Mumbai.

---

🔗 **Live Application:** [IntervuLog | Login](https://intervulog.onrender.com/login)  

---


## 🎥 Project Demonstration

*Student Portal — browsing experiences, submitting new ones, and managing profiles.*
![IntervuLog Demo](./assets/StudentDemo.gif)  

*SuperAdmin Dashboard — moderating submissions, managing users and roles.*
![IntervuLog Demo](./assets/SuperAdminDemo.gif)  


---

## ⚙️ Tech Stack

| Category | Technology | Purpose |
| :--- | :--- | :--- |
| **Backend** | **Spring Boot 3+** | Application core, Dependency Injection, Auto-configuration |
| **Security** | **Spring Security** | OAuth2 (Google) authentication, RBAC, "Remember Me" functionality |
| **Database** | **PostgreSQL** | Primary data storage |
| **ORM/Persistence** | **Spring Data JPA & Hibernate** | Object-Relational Mapping |
| **Dynamic Queries** | **JPA Specifications** | Advanced, dynamic query building |
| **Frontend** | **Thymeleaf**, **HTML5**, **CSS** | Server-side rendering and presentation layer |
| **Data Format** | **JSON / JSONB** | For flexible storage of unstructured interview round data |

---

## 💾 Database Schema

The database structure is **normalized** and mirrors the entity relationships managed by **Spring Data JPA**.

| Table | Description                                       | Key Columns / Notes |
| :--- |:--------------------------------------------------| :--- |
| **user_details** | Stores user profiles and authentication context.  | `email` (Unique), `user_role`, `firstName`, `department`, `passoutYear` |
| **internship_experiences** | Central fact table for all interview submissions. | Foreign keys to `companies`, `locations`, and `user_details`. Contains `rounds` (JSONB) and `status` (`PENDING` / `APPROVED`). |
| **companies** | Lookup table for unique company names.            | Linked via `company_id` |
| **locations** | Lookup table for unique location names.           | Linked via `location_id` |
| **persistent_logins** | Manages the Spring Security “Remember Me” tokens. | Auto-generated table for persistent session management |

---

## ✨ Core Features & Implementation Details

### 1. Secure Authentication & User Onboarding

- **OAuth2 Integration:**  
  Implemented custom logic within the `SecurityConfig`'s `OAuth2UserService` to auto-provision new users, assign default `STUDENT` roles, and enforce an email domain whitelist.

- **Mandatory Profile Completion:**  
  The `ProfileCompletionInterceptor` checks the `User` entity for missing required fields (like `department`, `passoutYear`).  
  If incomplete, users are redirected to `/userForm` and blocked from accessing `/dashboard` or other features until their profile is complete.

---

### 2. Experience Submission & JSON Handling

- **Submission Workflow:**  
  `InternshipSubmissionController` handles submissions, linking experiences to `User`, `Company`, and `Location` entities (creating new ones if needed).

- **Interview Rounds (JSONB):**  
  Interview rounds and their details are stored as a **JSON string** in the `rounds` column (`jsonb` type in PostgreSQL).  
  This flexible design supports schema evolution without migrations.

---

### 3. Role-Based Moderation System

- **Status Management:**  
  New submissions start with `ExperienceStatus.PENDING`.

- **Admin/SuperAdmin Access:**  
  Controllers secured with `@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")`.

- **Moderation Flow:**  
  `AdminExperienceController` allows reviewing, approving (`APPROVED`), or deleting pending experiences.

---

### 4. Dynamic Experience Browser

- **Advanced Filtering:**  
  The `ExperienceSpecification.java` class builds dynamic criteria using **JPA Criteria API**, supporting up to **10+ filters** like `year`, `company`, `ctcMin`, `stipendMax`, etc.

- **Public Visibility:**  
  Only experiences with `ExperienceStatus.APPROVED` are displayed publicly, ensuring data quality.

---

### 5. Superadmin Functionality

- **User Management:**  
  `SuperadminController` enables managing all users.

- **Role Updates:**  
  SuperAdmins can promote/demote users (`STUDENT` ↔ `ADMIN`) via `/users/update-role`.

- **Safe User Deletion:**  
  Implemented using `userRepository.deleteById()` with proper checks.

---

## 🛠️ How to Run Locally

### Prerequisites
- Java 17+
- Maven
- PostgreSQL

### Steps

1. **Clone the Repository:**
   ```bash
   git clone [YOUR-REPO-LINK]
   cd IntervuLog
   ```

2. **Database Setup:**

   * Create a PostgreSQL database (e.g., `intervulog_db`).
   * Update `src/main/resources/application.properties`:

     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/intervulog_db
     spring.datasource.username=your_user
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```

3. **Security Configuration:**

   * Set up a Google OAuth2 application and get your credentials.
   * Add them to `application.properties`:

     ```properties
     spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
     spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
     # Add your test email to the domain whitelist in SecurityConfig.java
     ```

4. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access:**
   Open [http://localhost:8080/](http://localhost:8080/) in your browser.

---

## 📝 Future Improvements

1. **Service Layer Abstraction:**  
   Refactor experience submission logic into a dedicated **Service Layer** to eliminate code duplication between controllers.

2. **Asynchronous Operations:**  
   Implement **@Async** for email notifications to admins (on submission) and users (on approval).

3. **Caching:**  
   Integrate **Redis** or **Caffeine** cache for frequently accessed data like companies and locations.

4. **Testing:**  
   Add **JUnit** and **Mockito** test suites for controller and service layer to ensure high test coverage.

5. **AI-Powered Summaries (Spring AI):**  
   Integrate **Spring AI** to automatically analyze and summarize companywise interview experiences, extracting common questions, topics, and insights — providing students with quick, AI-generated overviews.

---

## 📜 License

This project is intended solely for educational and portfolio demonstration purposes.  
© 2025 Kshitij Mahale — All Rights Reserved.
---
