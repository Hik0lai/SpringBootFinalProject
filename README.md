# Beehive Monitor - Spring Boot Main Final Project

A React-based frontend application for monitoring beehives, inspections, alerts, and user management.

## Features

- **Authentication**: Login and registration system
- **Dashboard**: View all your beehives in a grid layout
- **Hive Management**: Add, edit, and view detailed information about hives
- **Inspections**: Track hive inspections with dates, inspectors, and notes
- **Alerts**: Monitor critical alerts for your hives
- **Profile**: View and manage user profile
- **Admin Panel**: Admin-only access to view all users

## Tech Stack

- **React** 18.2.0
- **React Router** 6.20.0 for routing
- **Axios** for API calls
- **Tailwind CSS** for styling
- **Vite** as the build tool

## Getting Started

### Prerequisites

- Node.js (v16 or higher recommended)
- npm or yarn

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

3. Open your browser and navigate to:
```
http://localhost:5173
```

### Building for Production

To create a production build:

```bash
npm run build
```

The built files will be in the `dist` folder.

### Preview Production Build

To preview the production build:

```bash
npm run preview
```

## Backend API

This frontend expects a Spring Boot backend running on `http://localhost:8080` with the following endpoints:

- `/api/auth/login` - POST - User login
- `/api/auth/register` - POST - User registration
- `/api/users/me` - GET - Get current user info
- `/api/users` - GET - Get all users (admin only)
- `/api/hives` - GET/POST - List or create hives
- `/api/hives/{id}` - GET/PUT - Get or update hive
- `/api/inspections` - GET/POST - List or create inspections
- `/api/inspections/{id}` - GET/PUT - Get or update inspection
- `/api/alerts` - GET - Get alerts
- `/api/sensors/last-readings` - GET - Get sensor readings

## Project Structure

```
src/
├── App.jsx                 # Main app component with routing
├── main.jsx               # Entry point
├── index.css              # Global styles with Tailwind
├── components/
│   └── Navbar.jsx         # Navigation bar component
├── contexts/
│   └── AuthContext.jsx    # Authentication context
└── pages/
    ├── Login.jsx          # Login page
    ├── Register.jsx       # Registration page
    ├── Dashboard.jsx      # Main dashboard
    ├── HiveDetails.jsx    # Hive details view
    ├── HiveForm.jsx       # Add/Edit hive form
    ├── InspectionList.jsx # List of inspections
    ├── InspectionForm.jsx # Add/Edit inspection form
    ├── AlertsPage.jsx     # Alerts page
    ├── ProfilePage.jsx    # User profile page
    └── AdminPanel.jsx     # Admin panel
```

## Authentication

The app uses JWT token-based authentication. Tokens are stored in localStorage and automatically included in API requests.

## Routing

- `/login` - Login page
- `/register` - Registration page
- `/` - Dashboard (protected)
- `/hives/:id` - Hive details (protected)
- `/add-hive` - Add new hive (protected)
- `/edit-hive/:id` - Edit hive (protected)
- `/inspections` - Inspections list (protected)
- `/add-inspection` - Add inspection (protected)
- `/edit-inspection/:id` - Edit inspection (protected)
- `/alerts` - Alerts page (protected)
- `/profile` - Profile page (protected)
- `/admin` - Admin panel (protected, admin only)

## License

This project is part of a Spring Boot final project.

