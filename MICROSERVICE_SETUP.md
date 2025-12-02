# Microservice Setup Guide

## Overview

The Beehive Monitor application now uses a **microservice architecture** with a separate Sensor Microservice.

## Architecture

```
┌─────────────────────┐         ┌──────────────────────┐
│   Main Application  │  REST   │  Sensor Microservice │
│   (Port 8080)       │ ──────> │   (Port 8081)        │
│                     │         │                      │
│ - User Management   │         │ - Sensor Data        │
│ - Hive Management   │         │   Generation        │
│ - Alerts            │         │                      │
│ - Inspections       │         │                      │
│ - Graphics          │         │                      │
└─────────────────────┘         └──────────────────────┘
```

## Running Both Applications

### Step 1: Start the Sensor Microservice

Open a terminal and run:

```bash
cd sensor-microservice
mvn spring-boot:run
```

The microservice will start on **http://localhost:8081**

### Step 2: Start the Main Application

Open another terminal and run:

```bash
# From the root directory
mvn spring-boot:run
```

The main application will start on **http://localhost:8080**

### Step 3: Start the React Frontend (if needed)

```bash
npm run dev
```

The frontend will start on **http://localhost:5173**

## Running in IntelliJ IDEA (Recommended)

Since Maven may not be in your PATH, running from IntelliJ is the easiest option.

### Step 1: Import Microservice as Maven Project

1. **File** → **Open** → Navigate to `sensor-microservice` folder
2. Select `pom.xml` → Click **Open as Project**
3. IntelliJ will import it as a separate project/module

**OR** if you want both in the same window:

1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. Click **+** → **Import Module**
3. Select `sensor-microservice/pom.xml`
4. Choose **Import module from external model** → **Maven**
5. Click **Next** → **Finish**

### Step 2: Create Run Configuration for Microservice

1. **Run** → **Edit Configurations...**
2. Click **+** → **Application**
3. Configure:
   - **Name**: `Sensor Microservice`
   - **Main class**: `com.beehivemonitor.sensor.SensorMicroserviceApplication`
   - **Working directory**: `$PROJECT_DIR$/sensor-microservice` (or browse to the folder)
   - **Use classpath of module**: Select the microservice module if available
   - **JRE**: Java 17
4. Click **OK**

### Step 3: Create Run Configuration for Main App

1. **Run** → **Edit Configurations...**
2. Click **+** → **Application**
3. Configure:
   - **Name**: `Beehive Monitor Main App`
   - **Main class**: `com.beehivemonitor.BeehiveMonitorApplication`
   - **Working directory**: `$PROJECT_DIR$`
   - **Use classpath of module**: `beehive-monitor`
   - **JRE**: Java 17
4. Click **OK**

### Step 4: Run Both Applications

1. Start **Sensor Microservice** first (click the green play button)
2. Wait a few seconds for it to start (check console for "Started SensorMicroserviceApplication")
3. Start **Beehive Monitor Main App** (click the green play button)

**Tip**: You can run both simultaneously by selecting both configurations and clicking the play button, or run them one after another.

## Verification

### Check Microservice is Running

Visit: http://localhost:8081/api/sensor-data/hive/1

You should see JSON with sensor data.

### Check Main Application

Visit: http://localhost:8080/api/sensors/realtime-data (with Authorization header)

The main app should successfully call the microservice.

## Fallback Behavior

If the microservice is unavailable, the main application will:
- Log a warning message
- Fall back to local sensor data generation
- Continue operating normally

This ensures the application remains functional even if the microservice is down.

## Configuration

### Main Application (`application.properties`)

```properties
sensor.microservice.url=http://localhost:8081
```

### Microservice (`sensor-microservice/src/main/resources/application.properties`)

```properties
server.port=8081
```

## Troubleshooting

### Issue: Main app can't connect to microservice

**Solution:**
1. Verify microservice is running: `curl http://localhost:8081/api/sensor-data/hive/1`
2. Check firewall settings
3. Verify port 8081 is not in use

### Issue: CORS errors

**Solution:**
- Microservice CORS is configured to allow `http://localhost:8080` and `http://localhost:5173`
- If using different ports, update `CorsConfig.java` in the microservice

### Issue: Microservice not generating data

**Solution:**
- Check microservice logs for errors
- Verify the microservice started successfully
- Test the endpoint directly: `curl -X POST http://localhost:8081/api/sensor-data/realtime -H "Content-Type: application/json" -d '{"hiveIds":[1]}'`

## Benefits of Microservice Architecture

1. **Separation of Concerns**: Sensor data generation is isolated
2. **Scalability**: Microservice can be scaled independently
3. **Technology Flexibility**: Can be rewritten in different languages
4. **Fault Isolation**: If microservice fails, main app can fallback
5. **Independent Deployment**: Deploy microservice separately from main app

