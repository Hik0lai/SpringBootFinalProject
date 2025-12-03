# How to Run Sensor Microservice

## Simplest Way: Use Terminal

1. **Open Terminal in IntelliJ**:
   - Press `Alt + F12`
   - Or go to **View** → **Tool Windows** → **Terminal**

2. **Run these commands**:
   ```bash
   cd sensor-microservice
   mvn spring-boot:run
   ```

That's it! The microservice will start on **port 8081**.

## What You'll See

```
Started SensorMicroserviceApplication in X.XXX seconds
Tomcat started on port(s): 8081 (http)
```

## Verify It's Running

Open browser and visit:
```
http://localhost:8081/api/sensor-data/hive/1
```

You should see JSON with sensor data.

## Stop the Service

Press `Ctrl + C` in the terminal where it's running.

## Why Use Terminal?

- ✅ Works immediately, no IntelliJ configuration needed
- ✅ No "outside source root" errors
- ✅ Reliable and simple
- ✅ You can see all the logs directly


