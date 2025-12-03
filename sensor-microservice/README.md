# Sensor Microservice

A separate Spring Boot microservice that generates and provides sensor data for beehives.

## Overview

This microservice acts as a standalone service that generates random sensor values (temperature, humidity, CO2, sound level, weight) for beehives. It is consumed by the main Beehive Monitor application via REST API.

## Port

The microservice runs on **port 8081** (main application runs on 8080).

## How to Run

### Option 1: Using Maven

```bash
cd sensor-microservice
mvn spring-boot:run
```

### Option 2: Build and Run JAR

```bash
cd sensor-microservice
mvn clean package
java -jar target/sensor-microservice-1.0.0.jar
```

### Option 3: Using IntelliJ IDEA

1. Open the `sensor-microservice` folder as a separate project, OR
2. Import the `sensor-microservice/pom.xml` as a Maven module
3. Run `SensorMicroserviceApplication.java`

## API Endpoints

### POST /api/sensor-data/realtime
Generate sensor data for multiple hives.

**Request Body:**
```json
{
  "hiveIds": [1, 2, 3]
}
```

**Response:**
```json
{
  "sensorData": {
    "1": {
      "temperature": 22.5,
      "externalTemperature": 20.3,
      "humidity": 45.2,
      "co2": 850,
      "soundLevel": 65.8,
      "weight": 8.2
    },
    "2": {
      "temperature": 24.1,
      "externalTemperature": 21.7,
      "humidity": 50.3,
      "co2": 920,
      "soundLevel": 70.2,
      "weight": 9.5
    }
  }
}
```

### GET /api/sensor-data/hive/{hiveId}
Generate sensor data for a single hive.

**Response:**
```json
{
  "temperature": 22.5,
  "externalTemperature": 20.3,
  "humidity": 45.2,
  "co2": 850,
  "soundLevel": 65.8,
  "weight": 8.2
}
```

### GET /api/sensor-data/realtime?hiveIds=1,2,3
Alternative GET endpoint for multiple hives.

## Sensor Value Ranges

- **Internal Temperature**: 15-30°C
- **External Temperature**: 15-30°C
- **Humidity**: 5-60%
- **CO₂**: 400-2000 ppm
- **Sound Level**: 40-100 dB
- **Weight**: 4-12 kg

## Integration with Main Application

The main application (`beehive-monitor`) is configured to call this microservice at:
- URL: `http://localhost:8081`
- Configured in: `src/main/resources/application.properties` as `sensor.microservice.url`

If the microservice is unavailable, the main application will fall back to local sensor data generation.

## CORS Configuration

The microservice allows requests from:
- `http://localhost:8080` (main application)
- `http://localhost:5173` (React frontend)

## Dependencies

- Spring Boot Web
- Lombok

No database required - this is a stateless service that generates random values on each request.


