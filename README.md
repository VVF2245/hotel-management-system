# Hotel Management System

A full-stack hotel management application developed with Java, Spring Boot and React. The project aims to simplify hotel operations by providing functionality for room reservations and housekeeping management.

## Features

### Room Management and Reservations
- Search for rooms based on their characteristics.
- Manage room reservation workflows.
- Organize room information to support the booking process.

### Housekeeping Management
- Track rooms that require cleaning.
- Monitor room cleaning status.
- Help coordinate housekeeping tasks and room availability.

## Tech Stack

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA (Hibernate)
- Bean Validation

**Frontend**
- React
- JavaScript

**Database**
- MySQL 8.4
- Docker Compose (local development database)

**Tools**
- Git
- GitHub
- Docker

## Architecture

The application is structured into a backend and a frontend:

- **Backend:** Provides the application logic and handles hotel management operations through Spring Boot.
- **Frontend:** Provides the user interface for interacting with the application using React.
- **Database:** MySQL stores users, guests, rooms, bookings, booking history and cleaning tasks. The backend accesses it through Spring Data JPA.

## Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 21
- Node.js and npm
- Docker Desktop (runs the MySQL database)
- Git

### Installation

1. Clone the repository

```bash
git clone https://github.com/VVF2245/Hotel-Management-System.git
```

2. Navigate to the project directory

```bash
cd Hotel-Management-System
```

3. Create your local environment file from the example and set your own passwords

```bash
cp .env.example .env
```

4. Start the database (see [Database](#database))

```bash
docker compose up -d
```

### Running the backend

The backend lives in the repository root. Run the Spring Boot application from your IDE or with Maven:

```bash
./mvnw spring-boot:run
```

On Windows, use:

```bash
mvnw.cmd spring-boot:run
```

### Running the Frontend

Open a separate terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

## Database

The local database is MySQL 8.4, run with Docker Compose ([docker-compose.yml](docker-compose.yml)). It is published on port **3307** so it does not clash with a MySQL or MariaDB server already using 3306.

### Configuration

Credentials are kept in a `.env` file in the project root. It is ignored by git; [.env.example](.env.example) lists the required keys:

| Variable | Used by | Purpose |
|---|---|---|
| `DB_URL` | Spring Boot | JDBC URL, `jdbc:mysql://localhost:3307/hotel_management` |
| `DB_USERNAME` | Docker and Spring Boot | Application database user |
| `DB_PASSWORD` | Docker and Spring Boot | Password of the application user |
| `MYSQL_ROOT_PASSWORD` | Docker | Password of the MySQL `root` user |
| `ADMIN_USERNAME` | Spring Boot | Username of the initial administrator (default `admin`) |
| `ADMIN_EMAIL` | Spring Boot | Email of the initial administrator |
| `ADMIN_INITIAL_PASSWORD` | Spring Boot | Initial administrator password; required while no administrator exists |

Docker Compose reads `.env` automatically. Spring Boot imports it through `spring.config.import` in [application.properties](src/main/resources/application.properties), so no system environment variables are needed. Real environment variables take precedence, which allows other environments to provide their own values.

On first start, the container creates the `hotel_management` database and the application user. Changing the credentials in `.env` later requires recreating the volume (`docker compose down -v`), which deletes the data.

### Initial administrator

When the application starts and no administrator exists, it creates one from `ADMIN_USERNAME`, `ADMIN_EMAIL` and `ADMIN_INITIAL_PASSWORD`. The password is stored only as a bcrypt hash, is never logged, and must be changed at first sign-in. If `ADMIN_INITIAL_PASSWORD` is empty, or the username already belongs to another user, the application refuses to start. Once an administrator exists, these variables are ignored.

### Schema

The schema is generated from the JPA entities (`spring.jpa.hibernate.ddl-auto=update`) while the data model is being defined. Versioned migrations with Flyway are planned before the business services are implemented.

| Table | Content |
|---|---|
| `users` | Staff accounts. Single-table inheritance; `user_type` is `RECEPTIONIST`, `HOUSEKEEPER` or `ADMINISTRATOR` |
| `guest` | Guests and their optional contact and identity document details |
| `room` | Room inventory: unique number, type, nightly price, active flag |
| `booking` | Stays: guest, room, check-in and check-out dates, status, creating receptionist |
| `booking_event` | Immutable booking history: created, modified, cancelled, checked in, checked out |
| `cleaning_task` | Cleaning work per room; its state is derived from the start and completion timestamps |

Records referenced by history are never physically deleted: users and rooms are deactivated, and bookings are cancelled. The conceptual model is in [docs/diagrams/conceptual_model.puml](docs/diagrams/conceptual_model.puml).

### Useful commands

| Action | Command |
|---|---|
| Start the database | `docker compose up -d` |
| Check its status | `docker compose ps` |
| View its logs | `docker compose logs mysql` |
| Open a MySQL shell | `docker exec -it hotel-mysql mysql -u <DB_USERNAME> -p hotel_management` |
| Stop it (data is kept) | `docker compose stop` |
| Remove the container (data is kept) | `docker compose down` |
| Remove the container and all data | `docker compose down -v` |

## Project Structure

```text
hotel-management-system/
├── src/                    # Spring Boot backend
├── frontend/
│   └── src/
├── docs/
│   ├── architecture/
│   ├── diagrams/
│   └── requirements/
├── docker-compose.yml      # Local MySQL database
├── .env.example            # Template for the local .env file
├── pom.xml
└── README.md
```

## What I learned

## Future Improvements

## Author

Luis Hernández Cuadrado

https://github.com/VVF2245