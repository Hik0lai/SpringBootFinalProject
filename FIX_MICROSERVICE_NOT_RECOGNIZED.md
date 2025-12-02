# Fix: Sensor Microservice Not Recognized by IntelliJ

## Problem
- `SensorMicroserviceApplication.java` is red (not recognized)
- No `sensor-microservice` appears in Maven tool window
- IntelliJ doesn't see it as a module

## Solution: Import as Maven Module

### Step 1: Import the Module

1. **Open Project Structure**:
   - Press `Ctrl + Alt + Shift + S`
   - OR go to **File** → **Project Structure**

2. **Import Module**:
   - Click the **+** button at the top (under Modules section)
   - Select **"Import Module"**

3. **Navigate to POM file**:
   - Browse to: `C:\Users\Nikolay\OneDrive\Documents\Java\SpringBootMainFinlProject\sensor-microservice`
   - Select the **`pom.xml`** file inside that folder
   - Click **OK**

4. **Choose Import Type**:
   - Select **"Import module from external model"**
   - Choose **Maven**
   - Click **Next**

5. **Import Settings**:
   - Leave all defaults checked
   - Click **Next** → **Next** → **Finish**

6. **Wait**:
   - IntelliJ will import the module
   - Wait for Maven dependencies to download (check bottom status bar)

### Step 2: Mark Source Folders

After import, you need to mark the source folders:

1. **Still in Project Structure** (or reopen it):
   - Press `Ctrl + Alt + Shift + S`

2. **Go to Modules** (left sidebar)

3. **Select `sensor-microservice`** from the list

4. **Click on "Sources" tab** (top tabs)

5. **Mark folders**:
   - Find `src/main/java` → should show as **Sources** (blue)
   - If not, select it and click **Sources** button
   - Find `src/main/resources` → should show as **Resources** (green)
   - If not, select it and click **Resources** button

6. **Click Apply** → **OK**

### Step 3: Alternative - Mark in Project View

If the above doesn't work, try this:

1. In IntelliJ **Project view** (left side)
2. Find `sensor-microservice` folder
3. **Right-click** on `sensor-microservice/src/main/java`
4. Select **"Mark Directory as"** → **"Sources Root"**
   - Folder should turn **BLUE**
5. **Right-click** on `sensor-microservice/src/main/resources`
6. Select **"Mark Directory as"** → **"Resources Root"**
   - Folder should turn **GREEN/BROWN**

### Step 4: Reload Maven

1. **Right-click** on `sensor-microservice/pom.xml`
2. Select **Maven** → **Reload Project**
3. Wait for it to finish

### Step 5: Invalidate Caches

1. **File** → **Invalidate Caches...**
2. Check **ALL boxes**
3. Click **"Invalidate and Restart"**
4. Wait for IntelliJ to restart

### Step 6: Verify in Maven Window

After restart:
1. Open **Maven tool window** (View → Tool Windows → Maven)
2. You should now see `sensor-microservice` in the list!
3. Expand it → Plugins → spring-boot → spring-boot:run

## Alternative: Just Create Run Configuration (Easier!)

If importing is too complicated, just create a run configuration that works:

1. **Run** → **Edit Configurations...**

2. Click **+** → **Maven**

3. Configure:
   - **Name**: `Sensor Microservice`
   - **Working directory**: 
     ```
     C:\Users\Nikolay\OneDrive\Documents\Java\SpringBootMainFinlProject\sensor-microservice
     ```
     (Click folder icon to browse)
   
   - **Command line**: `spring-boot:run`

4. Click **OK**

5. Run it from the dropdown - it will work even if IntelliJ doesn't recognize it as a module!

## Quick Test

After importing:
- ✅ `SensorMicroserviceApplication.java` should NOT be red anymore
- ✅ You should see `sensor-microservice` in Maven window
- ✅ You can right-click the Java file and see "Run" option

