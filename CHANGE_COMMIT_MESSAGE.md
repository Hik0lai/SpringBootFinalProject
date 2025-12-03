# How to Change Commit Message

## Option 1: Complete the Interactive Rebase (Current Method)

You have the git-rebase-todo file open. Here's what to do:

### Step 1: Edit the rebase-todo file
1. Find the line with commit `679597a` (feat add greeting message...)
2. Change `pick` to `reword` (or just `r` for short)
   - Example: `reword 679597a feat add greeting message to currently logged in user`
3. Save the file (Ctrl+S) and close it

### Step 2: Change the commit message
- Git will open another editor with the commit message
- Replace the message with: `feat: add telephone to registration form`
- Save and close

### Step 3: If there are more commits
- Git will continue rebasing any commits after 679597a
- Just save and close any other editor windows that appear

## Option 2: Cancel and Use Simpler Method (Recommended)

If the rebase is confusing, cancel it and we'll use a simpler method:

### Cancel the rebase:
1. Close the editor without saving, OR
2. Delete everything in the file, OR
3. Run in terminal: `git rebase --abort`

Then I can help you use `git commit --amend` or another method.

## Which Option Do You Prefer?

- **Option 1**: Continue with the rebase (follow steps above)
- **Option 2**: Cancel and use simpler method (I'll guide you)


