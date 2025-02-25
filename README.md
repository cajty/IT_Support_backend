
# IT Support Ticket System - Backend

This is the backend component of the IT Support Ticket System, a Spring Boot application that provides RESTful APIs for managing IT support tickets.

## Overview

The IT Support Backend is a Java 17 Spring Boot application that provides RESTful API endpoints for ticket management, user authentication, and audit logging. The application uses Oracle SQL for data persistence.

## Project Structure

```
IT_Support_backend/
├── .idea/                # IntelliJ IDEA configuration files
├── .mvn/                 # Maven wrapper files
├── db/                   # Database scripts and schema
├── src/
│   └── main/
│       └── java/
│           └── org.ably.it_support/
│               ├── auditLog/          # Audit logging functionality
│               ├── auth/              # Authentication and authorization
│               ├── category/          # Category management
│               ├── comment/           # Ticket comments functionality
│               ├── core/            # Common utilities and config
│               ├── ticket/            # Core ticket management
│               ├── user/              # User management
│               └── ITSupportBackendApplication.java  # Main application class
```

## Features

- **RESTful API**: Full-featured API for ticket management
- **Authentication**: JWT-based authentication for secure access
- **Role-based Access Control**: Different access levels for employees and IT support staff
- **Ticket Management**: Create, update, and track support tickets
- **Commenting System**: Add comments to tickets
- **Audit Logging**: Track all changes to tickets
- **Category Management**: Organize tickets by predefined categories
- **Swagger/OpenAPI**: Comprehensive API documentation

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- Oracle Database (containerized via Docker)

## Setup and Installation

### Option 1: Using Docker (Recommended)

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd IT_Support_backend
   ```

2. Build and run using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   This will start both the Oracle database and the Spring Boot application.

3. The API will be available at: `http://localhost:8080`
   Swagger UI: `http://localhost:8080/swagger-ui.html`

### Option 2: Manual Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd IT_Support_backend
   ```

2. Configure Oracle database connection in `src/main/resources/application.yml`

3. Run the database scripts from the `db` directory to set up the schema

4. Build the application:
   ```bash
   ./mvnw clean package
   ```

5. Run the application:
   ```bash
   java -jar target/it-support-backend.jar
   ```

## API Endpoints

The API provides the following main endpoints:

### Authentication
- `POST /api/auth/login` - Authenticate user and get JWT token
- `POST /api/auth/register` - Register a new user (employee)

### Tickets
- `GET /api/tickets` - Get all tickets (IT support) or user's own tickets (employees)
- `GET /api/tickets/{id}` - Get a specific ticket
- `POST /api/tickets` - Create a new ticket
- `PUT /api/tickets/{id}` - Update a ticket
- `PATCH /api/tickets/{id}/status` - Update ticket status

### Comments
- `GET /api/tickets/{id}/comments` - Get all comments for a ticket
- `POST /api/tickets/{id}/comments` - Add a comment to a ticket

### Categories
- `GET /api/categories` - Get all ticket categories

### Users
- `GET /api/users` - Get all users (IT support only)
- `GET /api/users/{id}` - Get a specific user

### Audit Logs
- `GET /api/audit-logs` - Get audit logs (IT support only)
- `GET /api/tickets/{id}/audit-logs` - Get audit logs for a specific ticket

## Testing

Run the tests using Maven:

```bash
./mvnw test
```

## Docker Support

The application includes a `Dockerfile` and `docker-compose.yml` for containerization:

- `Dockerfile`: Builds the Spring Boot application
- `compose.yml`: Orchestrates the application and Oracle database

## Security

- JWT-based authentication
- Role-based access control
- Password encryption using BCrypt
- HTTPS support (configure in production)

## Development

### Adding a New Endpoint

1. Create a new controller class or add methods to an existing one
2. Annotate with appropriate request mappings and security controls
3. Implement service layer logic
4. Update API documentation
5. Write tests

### Database Migrations

Database schema changes should be tracked in the `db` directory with version numbers.

## Troubleshooting

Common issues:

- **Database connection errors**: Check Oracle credentials and availability
- **Authentication issues**: Ensure JWT token is properly included in requests
- **Permission errors**: Verify user roles and access controls

## License

[Include your license information here]