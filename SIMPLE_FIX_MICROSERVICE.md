# Simple Fix: Import Sensor Microservice Module

## The Problem
- File is red (IntelliJ doesn't recognize it)
- No sensor-microservice in Maven window
- IntelliJ hasn't imported it as a module

## Simple Fix: Import as Module

### Step-by-Step:

1. **Open Project Structure**:
   - Press: `Ctrl + Alt + Shift + S`
   - OR: **File** → **Project Structure**

2. **Click the + Button**:
   - Look at the top of the Modules section (left side)
   - Click the **+** button

3. **Select "Import Module"**:
   - Click **"Import Module"** from the dropdown

4. **Select the POM File**:
   - Navigate to: `sensor-microservice` folder
   - Select the **`pom.xml`** file inside it
   - Click **OK**

5. **Choose Maven**:
   - Select **"Import module from external model"**
   - Choose **Maven**
   - Click **Next**

6. **Click Next → Next → Finish**:
   - Leave all settings as default
   - Click **Next** a few times
   - Click **Finish**

7. **Wait for Import**:
   - IntelliJ will download dependencies
   - Wait for it to finish (check bottom status bar)

8. **Click OK** to close Project Structure

9. **Mark Source Folders**:
   - In Project view (left side), find `sensor-microservice`
   - **Right-click** on `sensor-microservice/src/main/java`
   - Select **"Mark Directory as"** → **"Sources Root"**
   - **Right-click** on `sensor-microservice/src/main/resources`
   - Select **"Mark Directory as"** → **"Resources Root"**

10. **Reload Maven**:
    - **Right-click** on `sensor-microservice/pom.xml`
    - Select **Maven** → **Reload Project**

## After This:

✅ The file should NOT be red anymore
✅ `sensor-microservice` should appear in Maven window
✅ You can run it from Maven window or create run configuration

## If Still Not Working - Use Run Configuration Instead:

Don't worry about IntelliJ recognizing it - just create a run configuration:

1. **Run** → **Edit Configurations...**
2. Click **+** → **Maven**
3. **Name**: `Sensor Microservice`
4. **Working directory**: Browse to `sensor-microservice` folder
5. **Command line**: `spring-boot:run`
6. Click **OK**

This will work even if IntelliJ doesn't recognize it as a module!


