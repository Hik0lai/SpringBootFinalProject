# Fix: Could not find or load main class - Complete Solution

## Problem
You're getting: `Error: Could not find or load main class com.beehivemonitor.BeehiveMonitorApplication`

This happens because **the project won't compile** due to missing classes (like `AuthResponse`), so the main class isn't generated in `target/classes`.

## Complete Fix Steps

### Step 1: Verify Source Folders Are Marked (CRITICAL!)

**This is the #1 cause of compilation failures!**

1. In IntelliJ **Project view**:
   - Find `src/main/java` folder
   - **Right-click** → **Mark Directory as** → **Sources Root**
   - The folder icon should turn **BLUE** ✅

2. Mark `src/main/resources`:
   - **Right-click** → **Mark Directory as** → **Resources Root**  
   - Should turn **green/brown** ✅

3. Mark `src/test/java`:
   - **Right-click** → **Mark Directory as** → **Test Sources Root**
   - Should turn **green** ✅

### Step 2: Verify File Content

1. Open `src/main/java/com/beehivemonitor/dto/AuthResponse.java`
2. **Verify it has content** (should NOT be empty!)
3. If empty, I just recreated it - **refresh the file** (close and reopen)

### Step 3: Enable Lombok Annotation Processing

1. **File → Settings** (`Ctrl+Alt+S`)
2. **Build, Execution, Deployment → Compiler → Annotation Processors**
3. ✅ **Check "Enable annotation processing"**
4. Click **Apply** then **OK**

### Step 4: Install/Enable Lombok Plugin

1. **File → Settings → Plugins**
2. Search for **"Lombok"**
3. If not installed, **Install** it (by Michail Plushnikov)
4. If installed but disabled, **Enable** it
5. **Restart IntelliJ** if prompted

### Step 5: Reload Maven Project

1. **Right-click on `pom.xml`**
2. Select **Maven → Reload Project**
3. Wait for Maven to finish (check bottom status bar)

### Step 6: Invalidate Caches

1. **File → Invalidate Caches...**
2. Check **ALL** boxes:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **"Invalidate and Restart"**
4. **Wait for IntelliJ to restart completely**

### Step 7: Clean and Rebuild Project

1. **Build → Clean Project**
2. Wait for it to finish
3. **Build → Rebuild Project**
4. Wait for compilation to finish

**Check for errors:**
- View → Tool Windows → Problems
- Or look at the bottom status bar for error count

### Step 8: Check Project Structure

1. **File → Project Structure** (`Ctrl+Alt+Shift+S`)
2. Go to **Modules**
3. Select your module
4. Under **Sources** tab:
   - `src/main/java` should be marked as **Sources** ✅
   - `src/main/resources` should be marked as **Resources** ✅
   - `src/test/java` should be marked as **Test Sources** ✅
5. Click **OK**

### Step 9: Fix Run Configuration

1. **Run → Edit Configurations...**
2. Find your Spring Boot run configuration
3. Verify:
   - **Main class**: `com.beehivemonitor.BeehiveMonitorApplication`
   - **Use classpath of module**: Select your module
   - **Working directory**: Should be project root
4. Click **OK**

### Step 10: Try Running Again

1. Click the **green play button** (or `Shift+F10`)
2. Check the **Run** tab for compilation errors
3. If errors persist, check **Problems** tab

## Quick Verification Checklist

Before running, verify:

- [ ] `src/main/java` is marked as **Sources Root** (blue folder)
- [ ] `src/main/resources` is marked as **Resources Root** (green folder)
- [ ] Lombok plugin is **installed and enabled**
- [ ] Annotation processing is **enabled**
- [ ] `AuthResponse.java` file has **content** (not empty)
- [ ] Project **rebuilds successfully** (no compilation errors)
- [ ] Run configuration has correct **main class**

## Expected Result

After these steps:

1. ✅ **Build → Rebuild Project** succeeds (no errors)
2. ✅ `target/classes/com/beehivemonitor/BeehiveMonitorApplication.class` exists
3. ✅ **Run configuration** works
4. ✅ Application starts: `Started BeehiveMonitorApplication in X seconds`

## Most Common Issue

**90% of the time, the problem is:**
- `src/main/java` is **NOT marked as Sources Root**

**Solution:**
1. Right-click `src/main/java` → Mark Directory as → Sources Root
2. Build → Rebuild Project
3. Try running again

## If Still Not Working

### Option 1: Create New Run Configuration
1. **Run → Edit Configurations**
2. Click **+** → **Spring Boot**
3. Name: `Beehive Monitor`
4. Main class: `com.beehivemonitor.BeehiveMonitorApplication`
5. Module: Select your module
6. Click **OK**

### Option 2: Check Compilation Output
1. **Build → Rebuild Project**
2. Watch the **Build** tab at bottom
3. Look for specific compilation errors
4. Fix those errors first

### Option 3: Manual Verification
1. Open file explorer to: `target/classes/com/beehivemonitor/`
2. Check if `.class` files exist there
3. If folder doesn't exist or is empty, compilation failed
4. Fix compilation errors and rebuild

The file `AuthResponse.java` has been recreated. Make sure it's saved and the source folders are marked correctly!

