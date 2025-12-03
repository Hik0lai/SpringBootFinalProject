# Detailed Steps: Import Module - Step by Step

## Step-by-Step with Dialog Descriptions

### Step 1: Open Project Structure
- Press: `Ctrl + Alt + Shift + S`
- OR: **File** → **Project Structure**

### Step 2: Click + Button
- In the left sidebar, you'll see "Modules" section
- At the TOP of that section, there's a **+** button
- Click that **+** button

### Step 3: Select Import Module
- A menu will appear with options like:
  - New Module
  - Import Module
  - Create Module from Existing Sources
  - etc.
- Click on **"Import Module"**

### Step 4: Browse for POM File
- A file browser window will open
- Navigate to: `C:\Users\Nikolay\OneDrive\Documents\Java\SpringBootMainFinlProject\sensor-microservice`
- Look for the **`pom.xml`** file inside that folder
- Select the **`pom.xml`** file
- Click **OK** or **Open**

### Step 5: Choose Import Type - THIS IS WHERE YOU ARE!

After selecting the pom.xml, you'll see a dialog that says:

**"Select the method you want to use to import the module:"**

You'll see several options (like radio buttons or checkboxes):
- ☐ Create module from existing sources
- ☐ Import module from external model  ← **SELECT THIS ONE!**
- ☐ Create module from existing sources (advanced)
- (possibly others)

**Action:**
- **Check/Select** the option that says **"Import module from external model"**
- Click **Next**

### Step 6: Choose Maven

On the NEXT screen, you'll see:

**"Select the external model:"**

You'll see a list or dropdown with options like:
- Maven ← **SELECT THIS ONE!**
- Gradle
- Eclipse
- etc.

**Action:**
- Click on **"Maven"** from the list
- Click **Next**

### Step 7: Import Settings

On the next screens:
- You'll see various import settings
- Leave everything as default (usually all checkboxes checked)
- Keep clicking **Next** (maybe 2-3 more times)
- Finally click **Finish**

### Step 8: Wait
- IntelliJ will now import the module
- Wait for it to finish (check bottom status bar)
- Click **OK** to close Project Structure

## Alternative: What If You Don't See These Options?

If the dialog is different, try this:

### Option A: Import via File Menu
1. **File** → **New** → **Module from Existing Sources...**
2. Navigate to `sensor-microservice` folder
3. Select the folder (not pom.xml)
4. Choose Maven from the import options

### Option B: Just Create Run Configuration Instead

If importing is too complicated, skip it and just create a run configuration:

1. **Run** → **Edit Configurations...**
2. Click **+** → **Maven**
3. **Name**: `Sensor Microservice`
4. **Working directory**: Click folder icon → Browse to `sensor-microservice` folder
5. **Command line**: `spring-boot:run`
6. Click **OK**

This works without needing to import as a module!

## Visual Guide of What You Should See:

```
Dialog Window Title: "Import Module"
┌─────────────────────────────────────────┐
│ Select the method to import:            │
│                                         │
│ ⚪ Create module from existing sources  │
│ ⚫ Import module from external model    │ ← Select this
│ ⚪ Create module from existing sources  │
│    (advanced)                           │
│                                         │
│          [Cancel]  [Next >]            │
└─────────────────────────────────────────┘

Then next screen:
┌─────────────────────────────────────────┐
│ Select the external model:              │
│                                         │
│ • Maven              ← Select this!     │
│   Gradle                                │
│   Eclipse                               │
│   etc.                                  │
│                                         │
│          [< Back]  [Next >]            │
└─────────────────────────────────────────┘
```


