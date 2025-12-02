# What Files to Commit for Others to Run the Project

## ✅ **MUST COMMIT** (Required for project to run)

### Core Application Files:
1. `src/main/java/com/beehivemonitor/config/DataInitializer.java` - Database seeding
2. `src/main/java/com/beehivemonitor/client/` - Feign Client interfaces
3. Modified tracked files:
   - `src/main/java/com/beehivemonitor/controller/UserController.java`
   - `src/main/java/com/beehivemonitor/service/UserService.java`
   - `src/main/resources/application.properties`
   - `src/pages/AdminPanel.jsx`

### Microservice DTOs:
4. `src/main/java/com/beehivemonitor/dto/MicroserviceRealtimeRequest.java`
5. `src/main/java/com/beehivemonitor/dto/MicroserviceRealtimeResponse.java`
6. `src/main/java/com/beehivemonitor/dto/MicroserviceSensorDataDTO.java`
7. `src/main/java/com/beehivemonitor/dto/NotificationRequest.java`
8. `src/main/java/com/beehivemonitor/dto/NotificationResponse.java`

### Microservices (if part of your project):
9. `sensor-microservice/` - Entire directory
10. `notification-microservice/` - Entire directory

## ⚠️ **OPTIONAL** (Nice to have, but not required)

- All `.md` documentation files (CHANGE_COMMIT_MESSAGE.md, FEIGN_MIGRATION.md, etc.)
  - These are helpful for understanding but won't prevent the project from running

## 🔒 **NEVER COMMIT** (Already ignored by .gitignore)

- `target/` - Maven build output
- `node_modules/` - NPM dependencies
- `.idea/` - IntelliJ IDE files
- `.env` - Environment variables
- `*.log` - Log files

---

**Bottom Line:** Without the required files above, the project will fail to compile or run when others clone it.

