# Midas Core

Midas Core is a transaction processing engine developed as part of the JPMC Advanced Software Engineering Forage program. It handles financial transactions between users, integrates with an external incentive service, and maintains a ledger of all activities.

## Project Overview

The application listens for transaction events on a Kafka topic, validates them against a database of users, calculates incentives via a REST API, and updates user balances accordingly.

### Key Features

- **Asynchronous Processing:** Consumes transaction messages from a Kafka topic (`midas-transactions`).
- **Transaction Validation:** Verifies sender/recipient existence and checks for sufficient funds.
- **Incentive Integration:** Calls an external service to apply bonuses to recipients.
- **Relational Storage:** Uses an H2 in-memory database to store user records and transaction history.
- **REST Endpoints:** Provides a `/balance` endpoint to query user balances.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.5** (Data JPA, Web, Kafka)
- **Apache Kafka**
- **H2 Database**
- **Maven**

## Configuration

The application is configured to run on port `33400` by default. Database and Kafka settings can be found in `src/main/resources/application.yml`.

- **H2 Console:** Accessible at `http://localhost:33400/h2-console` (JDBC URL: `jdbc:h2:mem:midasdb`)
- **Kafka Topic:** `midas-transactions`

## Getting Started

### Prerequisites

- JDK 17
- Maven

### Build and Run

To build the project and run the application:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Running Tests

The project includes several task-based test suites to verify functionality:

```bash
./mvnw test
```

## API Documentation

### Get User Balance
- **Endpoint:** `GET /balance?userId={id}`
- **Response:** Returns the current balance of the specified user.
