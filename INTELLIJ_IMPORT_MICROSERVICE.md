# Import Sensor Microservice as Maven Module in IntelliJ

## Problem
You're seeing:
- "Java file is located outside of the module source root"
- No option to run when clicking on `SensorMicroserviceApplication.java`
- Warning about the file being outside the source root

## Solution: Import as Maven Module

### Method 1: Import Module (Recommended - Keeps Everything in One Window)

1. **Open Project Structure**:
   - Press `Ctrl + Alt + Shift + S` 
   - Or go to **File** → **Project Structure**

2. **Import Module**:
   - Click the **+** button (top left, under "Modules")
   - Select **Import Module**

3. **Select the POM file**:
   - Navigate to: `sensor-microservice/pom.xml`
   - Select the `pom.xml` file
   - Click **OK**

4. **Choose Import Type**:
   - Select **"Import module from external model"**
   - Choose **Maven**
   - Click **Next**

5. **Import Options**:
   - Check all the boxes (import automatically, etc.)
   - Click **Next** → **Next** → **Finish**

6. **Wait for Import**:
   - IntelliJ will download Maven dependencies
   - Wait for it to finish (check bottom status bar)

7. **Mark Source Folders** (Important!):
   - In Project view, find `sensor-microservice`
   - Right-click on `sensor-microservice/src/main/java`
   - Select **Mark Directory as** → **Sources Root**
   - Right-click on `sensor-microservice/src/main/resources`
   - Select **Mark Directory as** → **Resources Root**

8. **Reload Maven Project**:
   - Right-click on `sensor-microservice/pom.xml`
   - Select **Maven** → **Reload Project**
   - Wait for it to finish

### Method 2: Open as Separate Project (Alternative)

If Method 1 doesn't work:

1. **File** → **Open**
2. Navigate to the `sensor-microservice` folder
3. Select the `pom.xml` file
4. Click **Open as Project**
5. IntelliJ will open it in a new window

## After Importing - Create Run Configuration

Once the module is imported:

1. **Run** → **Edit Configurations...**
2. Click **+** → **Application**
3. Configure:
   - **Name**: `Sensor Microservice`
   - **Main class**: `com.beehivemonitor.sensor.SensorMicroserviceApplication`
   - **Use classpath of module**: Select `sensor-microservice` (should appear in dropdown)
   - **Working directory**: `$MODULE_DIR$` (or browse to sensor-microservice folder)
   - **JRE**: Java 17
4. Click **OK**

Now you should be able to run it from the run dropdown!

## Quick Alternative: Use Terminal (Easier!)

If importing is too complicated, just use the terminal:

1. **Open Terminal in IntelliJ**: 
   - Press `Alt + F12`
   - Or **View** → **Tool Windows** → **Terminal**

2. **Navigate to microservice folder**:
   ```bash
   cd sensor-microservice
   ```

3. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```

This is actually the simplest way and works immediately!

## Verify It's Working

After importing, you should see:
- `sensor-microservice` appears as a module in Project Structure
- `src/main/java` is marked as Sources Root (blue folder)
- No more warnings about files being outside source root
- You can right-click on `SensorMicroserviceApplication.java` and see "Run" option

