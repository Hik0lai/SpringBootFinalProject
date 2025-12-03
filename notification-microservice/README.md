# Notification Microservice

A Spring Boot microservice for sending notifications (email, SMS, push) when alerts are triggered in the Beehive Monitor application.

## Overview

This microservice handles all notification-related functionality, including:
- Sending email notifications when alerts are triggered
- Storing notification history
- Managing notification templates (future enhancement)

## Port

The microservice runs on **port 8082** (main application: 8080, sensor microservice: 8081).

## Domain Entities

1. **Notification** - Stores notification records with status, recipient, message, and delivery information
2. **NotificationTemplate** - Reusable notification templates (for future use)

## How to Run

### Option 1: Using IntelliJ IDEA

1. **File** → **Open** → Navigate to `notification-microservice` folder
2. Select `pom.xml` → Click **Open as Project**
3. Find `NotificationMicroserviceApplication.java`
4. Right-click → **Run 'NotificationMicroserviceApplication.main()'**

### Option 2: Using Maven (if Maven is in PATH)

```bash
cd notification-microservice
mvn spring-boot:run
```

## Configuration

### Email Settings

Edit `src/main/resources/application.properties`:

```properties
# Gmail Example
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

**Important**: For Gmail, you need to:
1. Enable 2-Factor Authentication
2. Generate an App Password (not your regular password)
3. Use the App Password in `spring.mail.password`

### Other Email Providers

**Outlook/Hotmail:**
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
```

**Yahoo:**
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
```

## API Endpoints

### POST /api/notifications/send

Send a notification (email, SMS, or push).

**Request Body:**
```json
{
  "recipientEmail": "user@example.com",
  "subject": "Alert Triggered",
  "message": "Alert triggered: High Temperature Alert",
  "channel": "EMAIL",
  "alertId": 123
}
```

**Response:**
```json
{
  "notificationId": 1,
  "success": true,
  "message": "Notification sent successfully",
  "status": "SENT"
}
```

## Database

Currently uses H2 in-memory database. To use MySQL:

1. Update `pom.xml` to include MySQL connector
2. Update `application.properties` with MySQL connection details
3. Change `spring.jpa.database-platform` to `org.hibernate.dialect.MySQLDialect`

## Integration with Main Application

The main application calls this microservice when:
- An alert is triggered (status changes from false to true)
- User has `emailNotificationEnabled = true` in their profile

The email message format is: **"Alert triggered: [Alert Name]"**

## Troubleshooting

### Email not sending

1. Check email configuration in `application.properties`
2. Verify SMTP credentials are correct
3. For Gmail, ensure App Password is used (not regular password)
4. Check firewall/network settings
5. Review microservice logs for error messages

### Microservice not starting

1. Verify port 8082 is not in use
2. Check Java version (requires Java 17+)
3. Review application logs for errors

## Features

- ✅ Email notifications
- ✅ Notification history tracking
- ✅ Status tracking (PENDING, SENT, FAILED)
- ✅ Error handling and logging
- 🔄 SMS notifications (future)
- 🔄 Push notifications (future)
- 🔄 Notification templates (future)


