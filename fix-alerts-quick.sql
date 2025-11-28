-- Quick fix: Make old columns nullable
-- Run this in MySQL

USE beehivedb;

ALTER TABLE alerts MODIFY COLUMN message TEXT NULL;
ALTER TABLE alerts MODIFY COLUMN title VARCHAR(255) NULL;

