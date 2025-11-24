# IntelliJ IDEA Setup Guide

## Problem
You're getting `ERR_UNKNOWN_FILE_EXTENSION: Unknown file extension ".jsx"` because IntelliJ is trying to run React files directly.

## Solution: Run Backend and Frontend Separately

This is a **hybrid project** with:
- **Spring Boot Backend** (Java) - Run with Maven/IntelliJ
- **React Frontend** (JavaScript) - Run with npm/Vite

## Step-by-Step Setup

### 1. Configure IntelliJ for Spring Boot

1. **Open the project** in IntelliJ IDEA
2. **Import Maven project**: IntelliJ should auto-detect `pom.xml`
   - If prompted, click "Import Maven Project"
   - Wait for Maven dependencies to download

3. **Mark Spring Boot source folders**:
   - Right-click on `src/main/java` → Mark Directory as → Sources Root
   - Right-click on `src/main/resources` → Mark Directory as → Resources Root
   - Right-click on `src/test/java` → Mark Directory as → Test Sources Root

4. **Create Run Configuration for Spring Boot**:
   - Go to Run → Edit Configurations
   - Click `+` → Application
   - Name: `Beehive Monitor - Spring Boot`
   - Main class: `com.beehivemonitor.BeehiveMonitorApplication`
   - Use classpath of module: `beehive-monitor`
   - Click OK

5. **Run Spring Boot**: Click the green play button or press Shift+F10
   - Backend will start on: `http://localhost:8080`

### 2. Run React Frontend (Separate Terminal)

**DO NOT run React files through IntelliJ's Node.js runner!**

Instead:

1. **Open Terminal in IntelliJ**: View → Tool Windows → Terminal
   - Or press Alt+F12

2. **Run npm command**:
   ```bash
   npm run dev
   ```

3. Frontend will start on: `http://localhost:5173`

### 3. Alternative: Use IntelliJ's npm Run Configuration (Optional)

If you want to run it from IntelliJ:

1. Go to Run → Edit Configurations
2. Click `+` → npm
3. Name: `React Frontend - Vite`
4. Package.json: `package.json`
5. Command: `run`
6. Scripts: `dev`
7. Click OK

Now you can run both from IntelliJ's run configurations.

## Project Structure Explanation

```
SpringBootMainFinlProject/
├── src/                           # React Frontend (Vite handles JSX compilation)
│   ├── App.jsx                   # These need Vite to run, not Node.js directly
│   ├── pages/
│   └── ...
├── src/main/java/                # Spring Boot Backend (IntelliJ runs this)
│   └── com/beehivemonitor/
└── pom.xml                        # Maven config for Spring Boot
```

## Why This Error Happens

- **JSX files** (`.jsx`) need a **transpiler** (like Vite/Babel) to convert JSX to regular JavaScript
- Node.js **cannot** run `.jsx` files directly
- **Vite** handles this conversion when you run `npm run dev`
- IntelliJ's Node.js runner tries to run files directly, causing the error

## Quick Fix Checklist

✅ **IntelliJ Setup**:
- [ ] Import Maven project (if not auto-detected)
- [ ] Mark `src/main/java` as Sources Root
- [ ] Mark `src/main/resources` as Resources Root
- [ ] Create Spring Boot run configuration
- [ ] Run Spring Boot backend (should start on port 8080)

✅ **React Frontend**:
- [ ] Open Terminal in IntelliJ
- [ ] Run `npm run dev`
- [ ] Frontend should start on port 5173

✅ **Verify**:
- [ ] Backend running: http://localhost:8080
- [ ] Frontend running: http://localhost:5173
- [ ] Test login/register functionality

## Troubleshooting

### IntelliJ doesn't recognize Spring Boot
- File → Project Structure → Modules
- Ensure the module has `src/main/java` marked as Sources

### Maven dependencies not downloading
- View → Tool Windows → Maven
- Click the refresh icon (reload Maven projects)

### Port already in use
- Change port in `application.properties`: `server.port=8081`
- Or kill the process using the port

### Still getting JSX errors
- **Never** run `.jsx` files directly with Node.js
- **Always** use `npm run dev` to start the React frontend
- The `vite.config.js` handles JSX compilation

## Recommended IntelliJ Plugins

- **Spring Boot** (built-in)
- **Maven** (built-in)
- **Node.js** (for syntax highlighting, but don't use it to run JSX files)
- **Vite** (optional, for better Vite support)

