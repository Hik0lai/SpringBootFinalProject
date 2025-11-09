# How to Push Your Project to GitHub

## Step 1: Install Git (if not already installed)

1. Download Git for Windows from: https://git-scm.com/download/win
2. Run the installer and follow the setup wizard
3. Restart your terminal/PowerShell after installation
4. Verify installation by running: `git --version`

## Step 2: Configure Git (First time only)

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

## Step 3: Initialize Git Repository

```bash
# Navigate to your project directory (if not already there)
cd C:\Users\user\Programming\Web\SpringBootMainFinlProject

# Initialize git repository
git init

# Check status
git status
```

## Step 4: Add All Files to Git

```bash
# Add all files (respects .gitignore)
git add .

# Check what will be committed
git status
```

## Step 5: Create Initial Commit

```bash
git commit -m "Initial commit: React beekeeping management app"
```

## Step 6: Create GitHub Repository

1. Go to https://github.com and sign in
2. Click the "+" icon in the top right
3. Select "New repository"
4. Repository name: `SpringBootMainFinlProject` (or your preferred name)
5. Description: "Beekeeping Management System - React Frontend"
6. Choose Public or Private
7. **DO NOT** initialize with README, .gitignore, or license (we already have these)
8. Click "Create repository"

## Step 7: Connect Local Repository to GitHub

```bash
# Add remote repository (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/SpringBootMainFinlProject.git

# Verify remote was added
git remote -v
```

## Step 8: Push to GitHub

```bash
# Push to GitHub (first time)
git push -u origin main

# If you get an error about 'master' branch, use:
# git branch -M main
# git push -u origin main
```

## Step 9: Verify on GitHub

1. Refresh your GitHub repository page
2. You should see all your files uploaded!

## Future Updates

After making changes, use these commands:

```bash
git add .
git commit -m "Description of your changes"
git push
```

## Troubleshooting

### If Git is still not recognized:
- Restart your terminal/PowerShell
- Restart your computer after installing Git
- Check if Git is in your PATH environment variable

### If you get authentication errors:
- Use GitHub Personal Access Token instead of password
- Go to GitHub Settings > Developer settings > Personal access tokens
- Generate a new token with `repo` permissions
- Use the token as your password when pushing

### If branch name is 'master' instead of 'main':
```bash
git branch -M main
git push -u origin main
```

