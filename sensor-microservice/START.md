# How to Start Sensor Microservice

## Option 1: Using Maven (Recommended)

From the `sensor-microservice` folder, run:

```bash
mvn spring-boot:run
```

## Option 2: Build and Run JAR

```bash
# Build the project
mvn clean package

# Run the JAR file
java -jar target/sensor-microservice-1.0.0.jar
```

## Option 3: Using IntelliJ IDEA

1. Open `sensor-microservice/src/main/java/com/beehivemonitor/sensor/SensorMicroserviceApplication.java`
2. Right-click on the file
3. Select **Run 'SensorMicroserviceApplication'**

## What to Expect

After starting, you should see:
```
Started SensorMicroserviceApplication in X.XXX seconds
Tomcat started on port(s): 8081 (http)
```

The microservice will be available at: **http://localhost:8081**

## Verify It's Running

Open your browser and visit:
```
http://localhost:8081/api/sensor-data/hive/1
```

You should see JSON with sensor data.

## Stop the Microservice

Press `Ctrl + C` in the terminal where it's running.

