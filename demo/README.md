# **UVT Newcomer Assistant** 🎓

A full-stack application for helping UVT newcomers with onboarding tasks, contacts, events, and locations.

**Tech Stack:** Spring Boot 3.5.7 (Java 21) + React 19 + Vite + H2/MySQL

---

## ✅ **Lab 12 Requirements - ALL COMPLETED (13/13 points)**

| Requirement | Points | Status |
|-------------|--------|--------|
| 1. Presentation | 2 | ✅ Ready |
| 2. Unit Tests | 2 | ✅ 45+ tests |
| 3. Multithreading | 1 | ✅ AsyncConfig + ThreadPool |
| 5-8. Database | 3 | ✅ MySQL + H2 + JPA |
| 9. Input Validation | 1 | ✅ All endpoints |
| 11. REST CRUD | 3 | ✅ 8 endpoints |
| 12. Thymeleaf Views | 3 | ✅ 8 views |
| **TOTAL** | **13/13** | **✅ COMPLETE** |

See [LAB12_COMPLETE_GUIDE.md](LAB12_COMPLETE_GUIDE.md) for detailed requirements breakdown.

---

## 🚀 **Quick Start (Local Development)**

### **Prerequisites**
- Java 21+ (check: `java -version`)
- Maven 3.9+ (included via `mvnw`)
- Node.js 20+ LTS ([download](https://nodejs.org/)) for React frontend

### **Option 1: Using H2 In-Memory Database (Recommended for Testing)**

#### Backend (Spring Boot + H2):
```powershell
cd demo
$env:SPRING_PROFILES_ACTIVE='h2'
./mvnw spring-boot:run
```
API runs at **http://localhost:8080/api**

#### Frontend (React):
```powershell
cd demo/frontend
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
npm install
npm run dev
```
Web app runs at **http://localhost:5173**

### **Option 2: Using MySQL (Production Setup)**

#### 1. Install & Configure MySQL:
```sql
CREATE DATABASE newcomer_assistant_db;
CREATE USER 'uvt'@'localhost' IDENTIFIED BY 'uvt123';
GRANT ALL PRIVILEGES ON newcomer_assistant_db.* TO 'uvt'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/newcomer_assistant_db
spring.datasource.username=uvt
spring.datasource.password=uvt123
```

#### 3. Run Backend:
```powershell
cd demo
./mvnw spring-boot:run
```

#### 4. Run Frontend (same as H2 option):
```powershell
cd demo/frontend
npm install
npm run dev
```

---

## 🧪 **Run Tests**
```bash
./mvnw test
```
Runs all 45+ unit tests covering entities, repositories, and API controllers.

---

---

## 📖 **Project Background**

### **The Problem**

Based on research (interviews and surveys with UVT students), we identified that **new students, especially international students, are frustrated and overwhelmed.** Essential information is scattered across:

* Main UVT website
* StudentWeb and E-learning platforms
* Social media groups
* PDF documents  
* Physical bulletin boards

Newcomers waste time searching for simple information like classroom locations, professors' contacts, and student events—all while navigating language barriers.

**Goal:** Help UVT newcomers quickly find and trust all essential student information in one place.

### **Our Solution**

A **full-stack web application** with:
- **Backend:** Java Spring Boot REST API with MySQL/H2 database
- **Frontend:** React 19 + Vite modern SPA
- **Features:** User management, checklist tracking, universal search, contacts, events, locations

---

## 🏗️ **Architecture**

```
┌─────────────────┐      HTTP/JSON      ┌─────────────────┐
│   React App     │ ◄──────────────────► │  Spring Boot    │
│  (Vite dev)     │    CORS enabled     │   REST API      │
│  localhost:5173 │                      │  localhost:8080 │
└─────────────────┘                      └────────┬────────┘
                                                  │
                                         ┌────────▼────────┐
                                         │  H2 / MySQL DB  │
                                         │  (JPA/Hibernate)│
                                         └─────────────────┘
```

**Backend Stack:**
- Java 21
- Spring Boot 3.5.7 (Web, Data JPA, Thymeleaf)
- H2 (testing) / MySQL (production)
- Maven build
- Async/Threading support

**Frontend Stack:**
- React 19
- React Router 7
- Vite 7 (dev server + build)
- Modern CSS (no frameworks)

---

## 🔌 **API Endpoints**

All endpoints at `http://localhost:8080/api`

### **Users (CRUD)**
- `POST /users` - Create user (body: `{username, email, password}`)
- `GET /users` - List all users
- `GET /users/{id}` - Get user by ID
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user

### **Checklist**
- `GET /checklist` - Get all checklist items
- `POST /users/{userId}/checklist/{itemId}/check` - Mark item as done
- `POST /users/{userId}/checklist/{itemId}/uncheck` - Mark item as pending
- `GET /users/{userId}/checklist-progress` - Get completion stats

### **Content**
- `GET /contacts` - All university contacts
- `GET /events` - All newcomer events
- `GET /locations` - All campus locations
- `GET /search?q={query}` - Search across all content

### **Checklist Status (CRUD)**
- `GET /checklist-status` - All statuses
- `GET /checklist-status/{id}` - By ID
- `POST /checklist-status` - Create
- `PUT /checklist-status/{id}` - Update
- `DELETE /checklist-status/{id}` - Delete

**Input Validation:**
- Username: required, non-empty
- Email: required, must contain `@`
- Password: min 6 characters
- IDs: must be > 0

---

## 🖼️ **Web Interface Features**

Access at **http://localhost:5173** (React dev server)

1. **Home** - Dashboard with statistics
2. **Users** - Create, list, delete users
3. **Checklist** - Toggle onboarding tasks per user
4. **Search** - Universal search across contacts/events/locations
5. **Contacts** - University contact directory
6. **Events** - Newcomer events calendar
7. **Locations** - Campus locations guide

---

## 🧵 **Multithreading Support**

Configured in `AsyncConfiguration.java`:
- **Core pool size:** 5 threads
- **Max pool size:** 10 threads
- **Queue capacity:** 100 tasks
- **Thread prefix:** `async-`

Example async service: `AsyncUserService.java` with `@Async` methods.

---

## 📂 **Project Structure**

```
demo/
├── src/main/java/com/uvt/newcomerassistant/demo/
│   ├── config/
│   │   ├── AsyncConfiguration.java      # Multithreading
│   │   └── CorsConfig.java              # CORS for React
│   ├── controller/
│   │   ├── ApiController.java           # REST API
│   │   └── ViewController.java          # Thymeleaf views
│   ├── service/AsyncUserService.java    # Async operations
│   ├── User.java, ChecklistItemStatus.java  # JPA entities
│   ├── UserRepository.java, ChecklistItemStatusRepository.java
│   ├── AppData.java, JsonDataRepository.java
│   └── ChecklistItem.java, Contact.java, Event.java, Location.java
├── src/main/resources/
│   ├── application.properties           # MySQL config
│   ├── application-h2.properties        # H2 profile
│   ├── data.json                        # Seed data
│   └── templates/                       # Thymeleaf HTML
├── src/test/java/                       # 45+ unit tests
├── frontend/
│   ├── src/
│   │   ├── api/client.js                # API helper
│   │   ├── pages/                       # React pages
│   │   └── App.jsx, index.css, main.jsx
│   ├── .env                             # VITE_API_BASE_URL
│   └── package.json, vite.config.js
└── pom.xml
```

---

## 🐛 **Troubleshooting**

**Backend won't start - "Connection refused"**  
✅ Use H2 profile: `$env:SPRING_PROFILES_ACTIVE='h2'; ./mvnw spring-boot:run`

**Frontend CORS errors**  
✅ Check `CorsConfig.java` allows `http://localhost:5173`

**npm install fails - Execution policy**  
✅ `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`

**Port 8080 in use**  
✅ Change port: `./mvnw spring-boot:run -Dserver.port=8081`

---

## 📚 **Additional Documentation**

- [LAB12_COMPLETE_GUIDE.md](LAB12_COMPLETE_GUIDE.md) - Full implementation guide
- [PROJECT_REQUIREMENTS_SUMMARY.md](PROJECT_REQUIREMENTS_SUMMARY.md) - Requirements breakdown

---

## 🤝 **Contributing**

**Course:** Faculty of Mathematics and Informatics, UVT  
**Academic Year:** 2025-2026  
**License:** Educational project

---

**Questions?** Check the docs above or run tests. Good luck with your presentation! 🎓
