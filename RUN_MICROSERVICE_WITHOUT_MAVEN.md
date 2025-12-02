# Run Sensor Microservice Without Maven in PATH

## Problem
You get: `mvn : The term 'mvn' is not recognized`

This means Maven is not installed or not in your PATH. **No problem!** IntelliJ has its own Maven built-in.

## Solution 1: Use IntelliJ's Maven Tool Window (Easiest!)

1. **Open Maven Tool Window**:
   - Go to **View** → **Tool Windows** → **Maven**
   - Or click the **Maven** tab on the right side of IntelliJ

2. **Find sensor-microservice**:
   - Expand the project structure in Maven window
   - Look for `sensor-microservice` → `Plugins` → `spring-boot`

3. **Run the microservice**:
   - Double-click on **`spring-boot:run`** under `spring-boot` plugin
   - Or right-click → **Run 'sensor-microservice [spring-boot:run]'**

The microservice will start in the Run window at the bottom!

## Solution 2: Use IntelliJ's Run Configuration with Maven Goal

1. **Run** → **Edit Configurations...**

2. Click **+** → **Maven**

3. Configure:
   - **Name**: `Sensor Microservice`
   - **Working directory**: 
     ```
     C:\Users\Nikolay\OneDrive\Documents\Java\SpringBootMainFinlProject\sensor-microservice
     ```
     Or click folder icon and browse to `sensor-microservice` folder
   
   - **Command line**: `spring-boot:run`

4. Click **OK**

5. Select "Sensor Microservice" from run dropdown and click play button

## Solution 3: Create Application Run Configuration (If Module is Recognized)

If IntelliJ recognizes the microservice:

1. **Run** → **Edit Configurations...**

2. Click **+** → **Application**

3. Configure:
   - **Name**: `Sensor Microservice`
   - **Main class**: `com.beehivemonitor.sensor.SensorMicroserviceApplication`
   - **Working directory**: Browse to `sensor-microservice` folder
   - **Use classpath of module**: Select the module if available

4. Click **OK** and run

## Solution 4: Use IntelliJ's Terminal with Full Maven Path

If you know where IntelliJ's Maven is:

1. Open Terminal in IntelliJ (`Alt + F12`)

2. Run:
   ```powershell
   cd sensor-microservice
   & "C:\Program Files\JetBrains\IntelliJ IDEA\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
   ```

   (The path might be different - check your IntelliJ installation)

## Recommended: Solution 1 (Maven Tool Window)

**This is the easiest!** Just use IntelliJ's built-in Maven tool window - no PATH configuration needed.

## Verify It's Running

After starting, look for:
```
Started SensorMicroserviceApplication in X.XXX seconds
Tomcat started on port(s): 8081 (http)
```

Test it: Visit `http://localhost:8081/api/sensor-data/hive/1`

