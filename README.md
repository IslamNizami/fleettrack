# FleetTrack --- Vehicle Fleet Management System

FleetTrack is a monolithic backend application built with **Java 21**
and **Spring Boot**. It helps organizations manage vehicles, drivers,
maintenance schedules, and vehicle location updates through a
centralized platform.

## Features

### Vehicle management

-   Create, retrieve, update, and delete vehicle records.
-   Store vehicle details such as make, model, year, license plate, and
    status.
-   Support filtering, sorting, and pagination for list endpoints.

### Driver management

-   Maintain driver profiles, including license and contact information.
-   Track driver-to-vehicle assignments.

### Maintenance management

-   Record maintenance events and service history.
-   Schedule maintenance reminders.
-   Retain completed maintenance records for a configurable period and
    clean up old records automatically.

### Real-time vehicle locations

-   Stream vehicle location updates through WebSocket/STOMP.
-   Configure permitted WebSocket origins.

### Notifications and caching

-   Use Redis for caching and recent-notification storage.
-   Use Redis Pub/Sub to publish notification events where configured.
-   Support real-time notification delivery to subscribed components.

### Security and API quality

-   Secure endpoints with Spring Security and JWT-based authentication.
-   Support `ADMIN` and `FLEET_MANAGER` roles.
-   Validate incoming request data and provide centralized error
    handling.
-   Document the REST API with Swagger/OpenAPI.

### Reports

-   Generate PDF reports for supported fleet information.

## Technology Stack

  Technology                 Purpose
  -------------------------- ---------------------------------------------------------
  Java 21                    Application runtime
  Spring Boot                Backend application framework
  Gradle                     Build and dependency management
  PostgreSQL                 Relational data storage
  Flyway                     Versioned database migrations
  Spring Security + JWT      Authentication and role-based authorization
  Spring WebSocket / STOMP   Real-time messaging
  Redis                      Caching, notification channel, and recent notifications
  MapStruct                  Object mapping
  Swagger / OpenAPI          API documentation
  Docker                     Local infrastructure services

## Prerequisites

Install the following before running the application:

-   JDK 21
-   Docker and Docker Compose
-   Git
-   A Gradle installation is optional if the project includes the Gradle
    Wrapper (`./gradlew` or `gradlew.bat`).

The application also requires PostgreSQL and Redis. They can be run
locally or through Docker.

## Configuration

Application settings are managed in
`src/main/resources/application.properties`.

The project includes configurable schedules and notification settings
similar to the following:

``` properties
# Maintenance reminders
app.maintenance.reminder-days=3
app.maintenance.reminder-cron=0 0 8 * * *

# Cleanup of completed maintenance records
app.cleanup.cron=0 30 2 * * *
app.cleanup.completed-maintenance-retention-days=90

# Notifications
app.notifications.channel=fleet.notifications
app.notifications.recent-key=fleet:notifications:recent

# WebSocket
app.websocket.allowed-origins=*
```

### Configuration reference

  ----------------------------------------------------------------------------------------
  Property                                             Meaning
  ---------------------------------------------------- -----------------------------------
  `app.maintenance.reminder-days`                      Number of days before scheduled
                                                       maintenance to consider for a
                                                       reminder.

  `app.maintenance.reminder-cron`                      Cron schedule for running the
                                                       maintenance reminder job.
                                                       `0 0 8 * * *` runs daily at 08:00.

  `app.cleanup.cron`                                   Cron schedule for cleanup.
                                                       `0 30 2 * * *` runs daily at 02:30.

  `app.cleanup.completed-maintenance-retention-days`   Retention period for completed
                                                       maintenance records, in days.

  `app.notifications.channel`                          Name of the notification channel
                                                       used by the application.

  `app.notifications.recent-key`                       Redis key used for
                                                       recent-notification storage, if
                                                       implemented by the notification
                                                       service.

  `app.websocket.allowed-origins`                      Allowed browser origins for
                                                       WebSocket connections.
  ----------------------------------------------------------------------------------------

**Security note:** `*` is permissive and is generally suitable only for
local development. For production, configure the exact frontend
origin(s). Do not commit real passwords, JWT secrets, or other
credentials to version control.

> Cron jobs only run when scheduling is enabled and the corresponding
> scheduled methods are implemented. The properties define configuration
> values; the application code determines the precise reminder and
> cleanup behavior.

## Running the Application

### 1. Clone the repository

``` bash
git clone <repository-url>
cd <repository-directory>
```

Replace the placeholders with the actual repository URL and directory
name.

### 2. Start infrastructure

Start PostgreSQL and Redis using your project's Docker Compose
configuration, if one is provided:

``` bash
docker compose up -d
```

If the project does not include a Compose file, configure PostgreSQL and
Redis separately and ensure they are reachable by the application.

### 3. Configure the application

Review `src/main/resources/application.properties`. Set the database
connection, Redis connection, and security settings required by your
implementation.

Use environment variables or a local, untracked configuration file for
secrets. Ensure the configured PostgreSQL database exists and that the
application has the necessary permissions.

### 4. Run the application

On Linux or macOS:

``` bash
./gradlew bootRun
```

On Windows:

``` bat
gradlew.bat bootRun
```

If the Gradle Wrapper is not included, use an installed Gradle version:

``` bash
gradle bootRun
```

Flyway migrations should be applied automatically on startup when Flyway
is configured and enabled.

## API Documentation

When the application is running and Swagger/OpenAPI is configured, open
the API documentation at the Swagger UI URL configured by the project. A
common Springdoc default is:

``` text
http://localhost:8080/swagger-ui/index.html
```

The actual port and documentation path may differ according to the
application configuration.

## Testing

Run the automated test suite with the Gradle Wrapper.

Linux/macOS:

``` bash
./gradlew test
```

Windows:

``` bat
gradlew.bat test
```

Build the project:

``` bash
./gradlew build
```

Tests that require PostgreSQL or Redis need those services to be
available, unless the test configuration uses substitutes.

## Scheduled Jobs

The configured cron expressions use Spring's six-field format: **second
minute hour day-of-month month day-of-week**.

-   **Maintenance reminders:** daily at 08:00, using the configured
    three-day reminder window.
-   **Maintenance cleanup:** daily at 02:30, using the configured 90-day
    retention period.

These schedules use the application's configured timezone. Confirm the
deployment timezone if the jobs must run at a particular local time.
Cleanup behavior should be verified carefully to avoid deleting records
that must be retained.

## Project Structure

The exact package layout depends on the implementation. A typical
layered Spring Boot backend can be organized as follows:

``` text
src/
└── main/
    ├── java/
    │   └── .../
    │       ├── config/         # Security, WebSocket, and application configuration
    │       ├── controller/     # REST endpoints
    │       ├── dto/             # Request and response models
    │       ├── entity/          # Persistence entities
    │       ├── repository/      # Database access
    │       ├── service/         # Business logic
    │       └── ...              # Mapping, exceptions, scheduled jobs, etc.
    └── resources/
        ├── application.properties
        └── db/migration/        # Flyway migration scripts
```

Treat this as a suggested structure, not a guarantee of the current
repository layout.

## Notes

-   The backend is designed as a monolithic Spring Boot application.
-   Database schema changes should be managed through Flyway migrations.
-   Keep secrets out of source control.
-   Restrict WebSocket origins in production.
-   Verify role permissions, validation, pagination, scheduled jobs, and
    notification delivery as part of acceptance testing.

## License

Add the license and attribution details applicable to this repository.
