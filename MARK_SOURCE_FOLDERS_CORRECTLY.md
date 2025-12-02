# How to Mark Source Folders Correctly

## ❌ Don't Do This:
- Marking `sensor-microservice` directory as source root (wrong!)

## ✅ Do This Instead:

You need to mark **specific subdirectories**, not the whole folder:

### Step 1: Mark Java Source Folder

1. In Project view, find: `sensor-microservice`
2. Expand it to see: `src` → `main` → `java`
3. **Right-click** on: `sensor-microservice/src/main/java`
4. Select **"Mark Directory as"** → **"Sources Root"**
   - The folder icon should turn **BLUE** ✅

### Step 2: Mark Resources Folder

1. Still in `sensor-microservice`, find: `src` → `main` → `resources`
2. **Right-click** on: `sensor-microservice/src/main/resources`
3. Select **"Mark Directory as"** → **"Resources Root"**
   - The folder icon should turn **GREEN/BROWN** ✅

### Step 3: (Optional) Mark Test Folder

1. Find: `sensor-microservice/src/test/java` (if it exists)
2. **Right-click** on it
3. Select **"Mark Directory as"** → **"Test Sources Root"**
   - Should turn **GREEN** ✅

## What You Should See:

```
sensor-microservice/
├── src/
│   ├── main/
│   │   ├── java/          ← BLUE (Sources Root)
│   │   └── resources/     ← GREEN/BROWN (Resources Root)
│   └── test/
│       └── java/          ← GREEN (Test Sources Root)
```

## Why This Matters:

- **Sources Root** (`java`): Where your `.java` files are - IntelliJ compiles these
- **Resources Root** (`resources`): Where `application.properties` and other config files are
- **Test Sources Root**: Where your test files are

If you mark the whole `sensor-microservice` folder, IntelliJ will try to compile everything in it, including the `pom.xml`, `target/`, etc., which is wrong!

## After Marking:

1. The red error should disappear
2. IntelliJ will recognize the Java files
3. You might need to wait a few seconds for IntelliJ to index

## Still Not Working?

After marking folders:
1. **Right-click** on `sensor-microservice/pom.xml`
2. Select **Maven** → **Reload Project**
3. Wait for it to finish

This should fix the red file issue!

