-- SQL script to update the alerts table schema
-- Run this in MySQL to fix the schema mismatch
-- Execute this in MySQL command line or MySQL Workbench

USE beehivedb;

-- Step 1: Make old columns nullable (so inserts work without providing values)
ALTER TABLE alerts MODIFY COLUMN message TEXT NULL;
ALTER TABLE alerts MODIFY COLUMN title VARCHAR(255) NULL;

-- Step 2: Add new columns (will error if they already exist, which is OK)
-- Check if columns exist first, then add them
ALTER TABLE alerts ADD COLUMN name VARCHAR(255) NULL;
ALTER TABLE alerts ADD COLUMN trigger_conditions TEXT NULL;
ALTER TABLE alerts ADD COLUMN is_triggered BOOLEAN DEFAULT FALSE;

-- Step 3: Migrate existing data (copy title to name)
UPDATE alerts SET name = title WHERE name IS NULL AND title IS NOT NULL;
UPDATE alerts SET name = 'Alert' WHERE name IS NULL;

-- Step 4: Make name NOT NULL after data migration
ALTER TABLE alerts MODIFY COLUMN name VARCHAR(255) NOT NULL;

-- Step 5: Ensure hive_id is NOT NULL
ALTER TABLE alerts MODIFY COLUMN hive_id BIGINT NOT NULL;

