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
- Spring Security with OAuth2 Resource Server (JWT)
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
| `JWT_SECRET` | Spring Boot | Base64-encoded 256-bit key used to sign access tokens; generate with `openssl rand -base64 32`. The application refuses to start if it is missing, not Base64, or shorter than 256 bits |
| `AUTH_COOKIE_SECURE` | Spring Boot | `false` for local HTTP development (default); `true` in production so the access token cookie is only sent over HTTPS |

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

## Authentication

Staff sign in with a username and password. The backend is stateless: it keeps no server-side session.

### How it works

1. `GET /api/auth/csrf` returns `204` and sets the `XSRF-TOKEN` cookie. The frontend reads it and sends its value in the `X-XSRF-TOKEN` header on every `POST`, `PUT`, `PATCH` and `DELETE`, including the login.
2. `POST /api/auth/login` with `{ "username": "...", "password": "..." }` checks the credentials. On success it returns `200` with `username`, `role` and `mustChangePassword`, and sets the `access_token` cookie.
3. The browser sends that cookie automatically on later requests to `/api`. The backend validates the JWT it contains and applies the user's role.
4. `POST /api/auth/logout` (also with the `X-XSRF-TOKEN` header) clears the `access_token` cookie. It does not require a valid session, so an expired cookie can always be removed.

### Security decisions

- **The token lives in an `HttpOnly` cookie**, so JavaScript cannot read it and an XSS attack cannot steal it. It is never included in a response body.
- **`SameSite=Strict`, `Path=/api`**, plus **CSRF protection** with the double-submit cookie pattern. `Secure` is controlled by `AUTH_COOKIE_SECURE`.
- **Tokens are signed with HS256** using `JWT_SECRET` and expire after 1 hour (`app.jwt.expiration`). They carry the username, the role, and whether a password change is required. They never carry a password or a hash.
- **Every authentication failure returns the same `401`** (`Invalid username or password`), whether the password is wrong, the user does not exist, or the account is inactive, so the response does not reveal which usernames exist.
- Passwords are stored only as bcrypt hashes (see [Initial administrator](#initial-administrator)).
- **Logout removes the cookie but does not revoke the token.** A copied token stays valid until it expires (1 hour). Because the cookie is `HttpOnly`, page scripts cannot copy it. Real revocation would need refresh tokens or a denylist, which are out of scope for now.

### Trying it from a terminal

On Windows PowerShell 5.1, passing JSON inline to `curl.exe` strips the double quotes and the request body is malformed. Put the body in a file and use `--data @file` instead:

```powershell
'{"username":"admin","password":"<your ADMIN_INITIAL_PASSWORD>"}' | Set-Content -Encoding ascii login.json
```

```powershell
curl.exe -s -c jar.txt http://localhost:8080/api/auth/csrf
```

```powershell
$xsrf = (Get-Content jar.txt | Select-String 'XSRF-TOKEN\s+(\S+)$').Matches.Groups[1].Value
```

```powershell
curl.exe -i -b jar.txt -c jar.txt -H "X-XSRF-TOKEN: $xsrf" -H "Content-Type: application/json" --data "@login.json" http://localhost:8080/api/auth/login
```

Do not commit `login.json` or `jar.txt`: they contain credentials.

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