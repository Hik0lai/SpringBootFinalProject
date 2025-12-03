# How to Run Sensor Microservice in IntelliJ IDEA

## Problem
When trying to run the sensor microservice from IntelliJ's Maven section, you get:
```
Port 8080 is already in use
```

This happens because IntelliJ is running it from the wrong directory or using the wrong configuration.

## Solution: Create Proper Run Configuration

### Method 1: Create Application Run Configuration (Recommended)

1. **Open Run Configurations**:
   - Go to **Run** → **Edit Configurations...**
   - Or click the dropdown next to the run button → **Edit Configurations...**

2. **Create New Configuration**:
   - Click the **+** button (top left)
   - Select **Application**

3. **Configure the Run Configuration**:
   - **Name**: `Sensor Microservice`
   - **Main class**: `com.beehivemonitor.sensor.SensorMicroserviceApplication`
   - **Working directory**: Click the folder icon and browse to:
     ```
     C:\Users\Nikolay\OneDrive\Documents\Java\SpringBootMainFinlProject\sensor-microservice
     ```
     Or use: `$PROJECT_DIR$/sensor-microservice`
   
   - **Use classpath of module**: 
     - If the microservice is imported as a module, select it
     - Otherwise, leave it as the main module
   
   - **JRE**: Java 17 or higher

4. **Click OK**

5. **Run the Configuration**:
   - Select "Sensor Microservice" from the run dropdown
   - Click the green play button
   - It should start on **port 8081**

### Method 2: Run from Terminal in IntelliJ

1. **Open Terminal**:
   - **View** → **Tool Windows** → **Terminal**
   - Or press `Alt + F12`

2. **Navigate to microservice directory**:
   ```bash
   cd sensor-microservice
   ```

3. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```

### Method 3: Use Maven Goal with Correct Directory

If you want to use Maven from IntelliJ:

1. **Open Maven Tool Window**:
   - **View** → **Tool Windows** → **Maven**

2. **Expand sensor-microservice**:
   - Find `sensor-microservice` → `Plugins` → `spring-boot`
   - Right-click on `spring-boot:run`
   - Select **Create 'sensor-microservice [spring-boot:run]'...**

3. **Configure the Maven Run Configuration**:
   - **Name**: `Sensor Microservice`
   - **Working directory**: 
     ```
     $PROJECT_DIR$/sensor-microservice
     ```
   - **Command line**: `spring-boot:run`

4. **Click OK and Run**

## Verify It's Running Correctly

After starting, you should see in the console:
```
Started SensorMicroserviceApplication in X.XXX seconds
```

Check the port:
- Look for: `Tomcat started on port(s): 8081 (http)`
- It should say **8081**, NOT 8080

## Run Both Applications

Once the microservice is running on port 8081:

1. **Keep the microservice running**
2. **Start the main application** (should run on port 8080)
3. Both should now run in parallel!

## Troubleshooting

### Still getting port 8080 error?

1. **Check the working directory**:
   - Make sure it's set to `sensor-microservice` folder, not the root project

2. **Verify application.properties**:
   - Open `sensor-microservice/src/main/resources/application.properties`
   - Should have: `server.port=8081`

3. **Check for multiple instances**:
   - Make sure no other Spring Boot app is running
   - Kill any processes on port 8080/8081

### IntelliJ can't find the main class?

1. **Import microservice as module**:
   - **File** → **Project Structure** → **Modules**
   - Click **+** → **Import Module**
   - Select `sensor-microservice/pom.xml`
   - Choose **Maven** → **Next** → **Finish**

2. **Mark source folders**:
   - Right-click `sensor-microservice/src/main/java`
   - **Mark Directory as** → **Sources Root**

## Quick Checklist

- [ ] Working directory is set to `sensor-microservice` folder
- [ ] Main class is `com.beehivemonitor.sensor.SensorMicroserviceApplication`
- [ ] Port 8081 is free (not in use)
- [ ] Console shows "Started SensorMicroserviceApplication"
- [ ] Console shows port 8081 (NOT 8080)


