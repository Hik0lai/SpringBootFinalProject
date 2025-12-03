-- MySQL Setup Script for Beehive Monitor Application
-- Run this script as MySQL root user or admin

-- Create the database
CREATE DATABASE IF NOT EXISTS beehivedb;

-- Create the user 'sa' with password 'springboot'
CREATE USER IF NOT EXISTS 'sa'@'localhost' IDENTIFIED BY 'springboot';

-- Grant all privileges on beehivedb database to user 'sa'
GRANT ALL PRIVILEGES ON beehivedb.* TO 'sa'@'localhost';

-- Apply the changes
FLUSH PRIVILEGES;

-- Verify the user was created
SELECT User, Host FROM mysql.user WHERE User = 'sa';

-- Show databases to confirm beehivedb exists
SHOW DATABASES LIKE 'beehivedb';


