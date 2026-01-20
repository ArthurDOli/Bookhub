# Bookhub

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-green)
![Postgres](https://img.shields.io/badge/PostgreSQL-15-blue)

Library management system built with Spring Boot, implementing collection control, loans, and integration with the Google Books API.

---

## Overview

REST API for library management, including book registration, user management with different roles (READER and LIBRARIAN), loan management with limit and overdue validations, and automatic import of bibliographic data via the Google Books API. Authentication is handled using JWT and Spring Security.

---

## Technologies

- Java 21
- Spring Boot
- PostgreSQL
- JWT
- Docker & Docker Compose

---

## Project Structure

```
bookhub/
├── src/main/java/com/bookhub/bookhub/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── factory/
│   ├── filter/
│   ├── repository/
│   └── service/
├── src/main/resources/
│   └── application.yml
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## Main Features

- Full book management (CRUD, search by title/author/ISBN)
- Authentication system with JWT
- Loan control with validations
- Loan renewal
- Integration with Google Books API for book import
- Two user roles (READER and LIBRARIAN) with different permissions
- Documentation with Swagger/OpenAPI

---

## Installation and Execution

### With Docker (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ArthurDOli/Bookhub.git
   cd bookhub
   ```

2. **Configure environment variables:**
   ```bash
   export JWT_SECRET="your-secret-key"
   export GOOGLE_BOOKS_API_KEY="google-books-api-key"
   ```

3. **Start the containers:**
   ```bash
   docker-compose up -d
   ```

The application will be available at `http://localhost:8080`

### Without Docker

1. **Clone the repository:**

   ```bash
   git clone https://github.com/ArthurDOli/Bookhub.git
   cd bookhub
   ```

2. **Configure environment variables:**

   ```bash
   export JWT_SECRET="your-secret-key"
   export GOOGLE_BOOKS_API_KEY="google-books-api-key"
   export DB_USERNAME="postgres"
   export DB_PASSWORD="your-password"
   ```

3. **Run the application:**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

**Note:** PostgreSQL must be running locally on port 5432.

---

## API Documentation

Access the documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Authentication

To test the API, first create a user:

**POST** `/api/users/register`

```json
{
  "name": "Test Librarian",
  "email": "example@gmail.com",
  "password": "password123",
  "role": "LIBRARIAN"
}
```

Then log in:

**POST** `/api/auth/login`

```json
{
  "email": "example@gmail.com",
  "password": "password123"
}
```

Use the returned token in the `Authorization` header as `Bearer {token}` for subsequent requests.
