# **UVT Newcomer Assistant** 🎓

A full-stack application for helping UVT newcomers with onboarding tasks, contacts, events, and locations.

**Tech Stack:** Spring Boot 3.5.7 (Java 21) + React 19 + Vite + H2/MySQL

---

## ✅ **Lab 12 Requirements - ALL COMPLETED (13/13 points)**

| Requirement | Points | Status |
## 🔑 Project at a Glance

- **Backend:** Java 21, Spring Boot REST API, Thymeleaf views, JPA/Hibernate, JWT security, async thread pool.
- **Frontend:** React 19 + Vite SPA, React Router 7, API client via `VITE_API_BASE_URL`.
- **Data:** H2 in-memory by default (profile `h2`), MySQL supported for persistent use.
- **Tests:** 45+ JUnit tests (`./mvnw test`).

---

## 🧰 Prerequisites

- Java 21+ (LTS) — `java -version`
- Node.js 20+ LTS — `node -v`
- npm 10+ — `npm -v`
- Maven Wrapper included (`./mvnw` / `mvnw.cmd`), so no global Maven needed.

---

## ⚙️ Environment Variables

| File | Key | Purpose | Default |
| --- | --- | --- | --- |
| `demo/frontend/.env` | `VITE_API_BASE_URL` | Base URL for frontend API calls | `http://localhost:8080/api` |

For MySQL, set these (as env vars or in `application.properties`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/newcomer_assistant_db
SPRING_DATASOURCE_USERNAME=uvt
SPRING_DATASOURCE_PASSWORD=uvt123
```

> Tip: For local dev with H2, no extra env vars are needed; just activate the `h2` profile.

---

## 🚀 How to Run (Local)

### 1) Backend — Spring Boot

```powershell
cd demo
# Use H2 (recommended for quick start)
$env:SPRING_PROFILES_ACTIVE = "h2"
./mvnw spring-boot:run
```

- API available at `http://localhost:8080/api`
- Thymeleaf views at `http://localhost:8080`

#### MySQL mode (optional)
1) Create DB & user:
```sql
CREATE DATABASE newcomer_assistant_db;
CREATE USER 'uvt'@'localhost' IDENTIFIED BY 'uvt123';
GRANT ALL PRIVILEGES ON newcomer_assistant_db.* TO 'uvt'@'localhost';
FLUSH PRIVILEGES;
```
2) Set datasource env vars (see table above) or edit `src/main/resources/application.properties`.
3) Run: `./mvnw spring-boot:run`

### 2) Frontend — React + Vite

```powershell
cd demo/frontend
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass  # Windows script unblock
npm install
npm run dev
```

- Web app at `http://localhost:5173`
- Vite proxies API calls to `VITE_API_BASE_URL`

### 3) Run Tests

```powershell
cd demo
./mvnw test
```

---

## 📦 Production Build

- **Backend JAR:** `./mvnw clean package` → `target/demo-0.0.1-SNAPSHOT.jar`
- **Frontend build:**
    ```powershell
    cd demo/frontend
    npm run build
    npm run preview  # optional local preview
    ```
    Output in `demo/frontend/dist/` (serve via any static host or reverse-proxy behind the API).

---

## 🏗️ Architecture

```
┌─────────────────┐      HTTP/JSON      ┌─────────────────┐
│   React App     │ ◄──────────────────► │  Spring Boot    │
│  (Vite dev)     │    CORS enabled     │   REST API       │
│  localhost:5173 │                      │  localhost:8080 │
└─────────────────┘                      └────────┬────────┘
                                                                                                 │
                                                                                ┌────────▼────────┐
                                                                                │  H2 / MySQL DB  │
                                                                                │ (JPA/Hibernate) │
                                                                                └─────────────────┘
```

**Backend stack:** Spring Boot Web, Data JPA, Security, Thymeleaf, JWT, Async.

**Frontend stack:** React 19, React Router 7, Vite 7, modern CSS.

---

## 🔌 Key API Endpoints (all under `/api`)

- **Users:** `POST /users`, `GET /users`, `GET /users/{id}`, `PUT /users/{id}`, `DELETE /users/{id}`
- **Checklist:** `GET /checklist`, `POST /users/{userId}/checklist/{itemId}/check|uncheck`, `GET /users/{userId}/checklist-progress`
- **Content:** `GET /contacts`, `GET /events`, `GET /locations`, `GET /search?q=`
- **Checklist Status:** CRUD at `/checklist-status`

Validation highlights: username required, email contains `@`, password length ≥ 6, IDs > 0.

---

## 🖥️ UI Features

- Home dashboard
- Users CRUD
- Checklist per user
- Universal search
- Contacts directory
- Events calendar
- Locations guide

---

## 🧵 Multithreading

`AsyncConfiguration.java` sets a pooled executor (core 5, max 10, queue 100, prefix `async-`). `AsyncUserService` demonstrates `@Async` usage.

---

## 📂 Project Structure (simplified)

```
demo/
├── pom.xml
├── src/main/java/com/uvt/newcomerassistant/demo/
│   ├── config/ (Async, CORS, Security)
│   ├── controller/ (REST + Thymeleaf)
│   ├── security/ (JWT filter + util)
│   ├── service/ (async services)
│   ├── dto/entities/repositories
│   └── ...
├── src/main/resources/
│   ├── application.properties
│   ├── application-h2.properties
│   ├── data.json
│   └── templates/
├── src/test/java/ (45+ tests)
├── frontend/
│   ├── src/ (pages, components, context, api)
│   ├── vite.config.js
│   └── .env (VITE_API_BASE_URL)
└── README.md
```

---

## 🐛 Troubleshooting

- **Backend fails to start / DB connection refused:** use H2 profile: `$env:SPRING_PROFILES_ACTIVE='h2'; ./mvnw spring-boot:run`.
- **Frontend CORS errors:** ensure `CorsConfig` allows `http://localhost:5173`.
- **npm install blocked on Windows:** `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`.
- **Port 8080 busy:** `./mvnw spring-boot:run -Dserver.port=8081` and update `VITE_API_BASE_URL` accordingly.

---


## 🤝 Contributing & License

Course project (Faculty of Mathematics and Informatics, UVT, AY 2025-2026). Educational use.

---

Questions? Run the app with H2, hit `/api`, or open `frontend` in dev mode. 🎓
- **Queue capacity:** 100 tasks
