# Lombok Setup Guide for IntelliJ IDEA

## ✅ Lombok is Already in Your Project

Your `pom.xml` already includes Lombok (line 77-81). You just need to enable it in IntelliJ.

## Step-by-Step Setup

### Step 1: Install Lombok Plugin

1. **Open Settings**:
   - File → Settings (or press `Ctrl+Alt+S` on Windows/Linux)
   - Or `Cmd+,` on Mac

2. **Go to Plugins**:
   - In the left sidebar, click **Plugins**

3. **Search for Lombok**:
   - Type "Lombok" in the search box
   - Look for **"Lombok"** by Michail Plushnikov
   - Click **Install** (if not already installed)

4. **Restart IntelliJ**:
   - Click "Restart IDE" when prompted

### Step 2: Enable Annotation Processing

1. **Open Settings again**:
   - File → Settings (`Ctrl+Alt+S`)

2. **Navigate to Annotation Processors**:
   - Build, Execution, Deployment → Compiler → Annotation Processors

3. **Enable annotation processing**:
   - ✅ Check "Enable annotation processing"
   - Click **OK**

### Step 3: Reload Maven Project

1. **Reload Maven**:
   - Right-click on `pom.xml` → Maven → Reload Project
   - Or open Maven tool window (View → Tool Windows → Maven) → Click the refresh icon

2. **Rebuild Project**:
   - Build → Rebuild Project

### Step 4: Verify Setup

1. **Check Lombok is working**:
   - Open any DTO file (e.g., `AuthResponse.java`)
   - Hover over a Lombok annotation like `@Data`
   - You should see IntelliJ recognizing it (no red underlines)

2. **Check Structure**:
   - Right-click on a class with `@Data` → Show Bytecode or Show Structure
   - You should see getters/setters generated (though they're generated at compile time)

## Verification Checklist

✅ **Lombok Plugin Installed**: File → Settings → Plugins → Lombok is installed  
✅ **Annotation Processing Enabled**: Settings → Compiler → Annotation Processors → Enabled  
✅ **Maven Reloaded**: Right-click pom.xml → Maven → Reload Project  
✅ **Project Rebuilt**: Build → Rebuild Project  
✅ **No Red Underlines**: DTO classes should compile without errors  

## Common Issues & Solutions

### Issue: "Cannot find symbol: method getXxx()" or "@Data not found"

**Solution**:
1. Ensure Lombok plugin is installed
2. Enable annotation processing
3. Rebuild project: Build → Rebuild Project
4. Invalidate caches: File → Invalidate Caches → Invalidate and Restart

### Issue: "Symbol not found" errors for Lombok annotations

**Solution**:
1. Check `pom.xml` has Lombok dependency (it does - line 77-81)
2. Reload Maven project
3. Enable annotation processing
4. Rebuild project

### Issue: IntelliJ not recognizing Lombok

**Solution**:
1. Uninstall and reinstall Lombok plugin
2. Restart IntelliJ
3. Enable annotation processing
4. Rebuild project

## Lombok Annotations Used in This Project

- `@Data` - Generates getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` - Generates no-args constructor
- `@AllArgsConstructor` - Generates constructor with all fields

## Benefits of Using Lombok

✅ **Less boilerplate code** - No need to write getters/setters manually  
✅ **Cleaner classes** - More readable code  
✅ **Auto-generated** - Reduces errors from manual code  

## Alternative: Manual Getters/Setters (If You Want to Avoid Lombok)

If you prefer not to use Lombok, I can rewrite all DTOs and entities with manual getters/setters. Just let me know!

## Next Steps

After setting up Lombok:

1. ✅ Rebuild the project
2. ✅ Run Spring Boot application
3. ✅ Verify no compilation errors
4. ✅ Start backend on http://localhost:8080

Your project should now compile successfully! 🎉

