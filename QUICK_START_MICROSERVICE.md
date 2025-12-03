# Quick Start: Sensor Microservice in IntelliJ (No Maven PATH Needed)

## Method 1: Maven Tool Window (Easiest!)

1. **Open Maven Tool Window**:
   - Click the **Maven** tab on the right side of IntelliJ
   - OR go to **View** → **Tool Windows** → **Maven**

2. **Find sensor-microservice**:
   - In the Maven window, expand your project
   - Look for `sensor-microservice`
   - Expand it → `Plugins` → `spring-boot`
   - You should see `spring-boot:run`

3. **Run it**:
   - **Double-click** on `spring-boot:run`
   - OR right-click → **Run 'sensor-microservice [spring-boot:run]'**

4. **Wait for it to start**:
   - Look at the bottom "Run" tab
   - You should see: `Started SensorMicroserviceApplication in X.XXX seconds`
   - `Tomcat started on port(s): 8081 (http)`

## Method 2: Create Maven Run Configuration

1. **Run** → **Edit Configurations...**

2. Click **+** → Select **Maven**

3. Configure:
   - **Name**: `Sensor Microservice`
   - **Working directory**: 
     - Click the folder icon
     - Browse to: `sensor-microservice` folder
     - OR type: `$PROJECT_DIR$/sensor-microservice`
   
   - **Command line**: `spring-boot:run`

4. Click **OK**

5. Select "Sensor Microservice" from run dropdown and click the play button ▶️

## Method 3: If You Can See the Main Class

If IntelliJ recognizes the microservice properly:

1. Open: `sensor-microservice/src/main/java/com/beehivemonitor/sensor/SensorMicroserviceApplication.java`

2. Click the green arrow next to `public static void main` 

3. Select **"Run 'SensorMicroserviceApplication'"**

## Verify It's Running

After starting, check the console for:
```
Started SensorMicroserviceApplication in X.XXX seconds
Tomcat started on port(s): 8081 (http)
```

Test it: Open browser and visit:
```
http://localhost:8081/api/sensor-data/hive/1
```

You should see JSON with sensor data!

## Stop the Service

- Click the red square (Stop button) in the Run window
- OR close the Run window/tab

## Troubleshooting

**Can't find sensor-microservice in Maven window?**
- Right-click on `sensor-microservice/pom.xml` → **Maven** → **Reload Project**
- Wait for it to finish, then check Maven window again

**Still not working?**
- Use Method 2 (Maven Run Configuration) - it's more reliable!


