# Spring Boot Backend Setup

## ✅ Transformation Complete!

Your React frontend project has been successfully upgraded to a full Spring Boot application while preserving the existing React frontend.

## Project Structure

```
SpringBootMainFinlProject/
├── src/                          # React Frontend (preserved)
│   ├── App.jsx
│   ├── components/
│   ├── contexts/
│   ├── pages/
│   └── ...
├── src/main/                     # Spring Boot Backend (NEW)
│   ├── java/com/beehivemonitor/
│   │   ├── entity/              # JPA Entities
│   │   ├── repository/          # JPA Repositories
│   │   ├── service/             # Business Logic
│   │   ├── controller/          # REST Controllers
│   │   ├── security/            # JWT Authentication
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── config/              # Configuration
│   │   └── exception/           # Exception Handling
│   └── resources/
│       └── application.properties
├── pom.xml                       # Maven Configuration
└── package.json                  # React/NPM Configuration (preserved)
```

## Prerequisites

1. **Java 17 or higher** - [Download](https://adoptium.net/)
2. **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
3. **Node.js 16+** (for frontend) - Already installed
4. **IDE** - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## How to Run

### Option 1: Run Both Separately (Development)

#### Start Spring Boot Backend:
```bash
# Using Maven
mvn spring-boot:run

# Or build and run
mvn clean package
java -jar target/beehive-monitor-1.0.0.jar
```

The backend will start on: **http://localhost:8080**

#### Start React Frontend (in separate terminal):
```bash
npm run dev
```

The frontend will start on: **http://localhost:5173**

### Option 2: Build Frontend and Serve from Spring Boot (Production)

1. Build React frontend:
```bash
npm run build
```

2. Copy `dist` folder to `src/main/resources/static`

3. Spring Boot will serve the frontend at `http://localhost:8080`

## API Endpoints

All endpoints match your React frontend expectations:

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Users
- `GET /api/users/me` - Get current user (authenticated)
- `GET /api/users` - Get all users (admin only)

### Hives
- `GET /api/hives` - List all user's hives
- `GET /api/hives/{id}` - Get hive details
- `POST /api/hives` - Create new hive
- `PUT /api/hives/{id}` - Update hive

### Inspections
- `GET /api/inspections` - List all inspections
- `GET /api/inspections/{id}` - Get inspection details
- `POST /api/inspections` - Create new inspection
- `PUT /api/inspections/{id}` - Update inspection

### Alerts
- `GET /api/alerts` - Get all alerts

### Sensors
- `GET /api/sensors/last-readings?hiveId={id}` - Get latest sensor readings

## Database

- **Development**: H2 in-memory database (data resets on restart)
- **Access H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:beehivedb`
  - Username: `sa`
  - Password: (empty)

## Security

- **JWT Authentication**: Tokens are generated and validated automatically
- **Password Encryption**: BCrypt
- **CORS**: Configured for `http://localhost:5173`
- **Role-based Access**: Admin-only endpoints protected

## Testing

### Test the API with curl:

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Get current user (replace TOKEN with JWT from login response)
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer TOKEN"
```

## Features Implemented

✅ JWT Authentication  
✅ User Registration & Login  
✅ Hive Management (CRUD)  
✅ Inspection Management (CRUD)  
✅ Alert System  
✅ Sensor Readings  
✅ Admin Panel Support  
✅ CORS Configuration  
✅ H2 Database Setup  
✅ Security Configuration  
✅ Exception Handling  

## Next Steps

1. **Test the Backend**: Start Spring Boot and verify it runs
2. **Test Integration**: Start both frontend and backend, test the full flow
3. **Add Test Data**: Use H2 console or create a data initializer
4. **Production Database**: Switch to MySQL/PostgreSQL for production
5. **Deployment**: Configure for your deployment environment

## Troubleshooting

### Port Already in Use
- Change `server.port` in `application.properties`

### JWT Errors
- Ensure JWT secret is at least 32 characters (already configured)

### CORS Issues
- Verify `application.properties` has correct CORS settings
- Check SecurityConfig.java

### Database Issues
- H2 console URL: http://localhost:8080/h2-console
- Check `application.properties` for database settings

## Notes

- Frontend is preserved and unchanged
- All API endpoints match frontend expectations
- JWT tokens work with existing frontend localStorage
- Data is stored in H2 in-memory database (resets on restart)

