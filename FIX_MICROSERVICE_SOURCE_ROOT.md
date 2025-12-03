# Fix: Java file is located outside of module source root

## Quick Fix Steps

### Step 1: Open Project Structure

1. Press `Ctrl + Alt + Shift + S`
2. Or go to **File** → **Project Structure**

### Step 2: Check Modules

1. Click on **Modules** (left sidebar)
2. Look for `sensor-microservice` in the list
   - If you see it → Go to Step 3
   - If you DON'T see it → Go to Step 4 (Import Module first)

### Step 3: Mark Source Folders (If Module Exists)

1. Select **`sensor-microservice`** module in the list
2. Click on the **Sources** tab (top tabs)
3. You should see folders listed:
   - Find `src/main/java` → should show as **Sources** (blue)
   - Find `src/main/resources` → should show as **Resources** (green)
   
4. **If they're NOT marked correctly:**
   - Select `src/main/java` folder
   - Click the **Sources** button (or right-click → Mark as Sources)
   - Select `src/main/resources` folder  
   - Click the **Resources** button (or right-click → Mark as Resources)
   - Click **Apply** → **OK**

### Step 4: Import Module (If Module Doesn't Exist)

If `sensor-microservice` is NOT in the modules list:

1. Click the **+** button (top of the modules list)
2. Select **Import Module**
3. Navigate to: `C:\Users\Nikolay\OneDrive\Documents\Java\SpringBootMainFinlProject\sensor-microservice`
4. Select the folder and click **OK**
5. Choose **"Import module from external model"**
6. Select **Maven**
7. Click **Next** → **Next** → **Finish**
8. Wait for import to complete
9. Then go back to Step 3 to mark source folders

### Step 5: Alternative - Use File Explorer Method

If the above doesn't work:

1. In IntelliJ **Project view**, find the `sensor-microservice` folder
2. **Right-click** on `sensor-microservice/src/main/java`
3. Select **"Mark Directory as"** → **"Sources Root"**
   - The folder icon should turn **BLUE**
4. **Right-click** on `sensor-microservice/src/main/resources`
5. Select **"Mark Directory as"** → **"Resources Root"**
   - The folder icon should turn **GREEN/BROWN**

### Step 6: Invalidate Caches (If Still Not Working)

1. **File** → **Invalidate Caches...**
2. Check **ALL boxes**
3. Click **"Invalidate and Restart"**
4. Wait for IntelliJ to restart

### Step 7: Rebuild Project

1. **Build** → **Rebuild Project**
2. Wait for compilation

## Verify It's Fixed

After these steps:
- ✅ No more warning about "outside source root"
- ✅ `src/main/java` folder is **blue** (Sources Root)
- ✅ `src/main/resources` folder is **green** (Resources Root)
- ✅ You can right-click `SensorMicroserviceApplication.java` and see "Run" option

## Simplest Solution: Just Use Terminal!

**Honestly, the easiest way is to just use the terminal:**
- Press `Alt + F12` to open terminal
- Run: `cd sensor-microservice && mvn spring-boot:run`
- That's it! No need to fix IntelliJ's recognition.

The terminal approach works immediately and doesn't require any IntelliJ configuration!


