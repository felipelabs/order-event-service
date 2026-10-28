# Order Event Service

A Spring Boot service that accepts orders and publishes OrderCreated events through RabbitMQ using the transactional outbox pattern.

## Features

- Create and query orders through a versioned REST API
- Idempotent creation using a client-supplied reference
- Persist the order and its outbox event in one PostgreSQL transaction
- Retry transient RabbitMQ publishing failures with bounded exponential backoff
- Mark an event DEAD after eight unsuccessful delivery attempts
- Publish persistent messages to a durable topic exchange and queue
- Return validation and conflict errors as RFC 9457 ProblemDetail responses
- Manage schema changes with Flyway; Hibernate runs in validate mode

## Stack

Java 17 · Spring Boot 3.5 · Spring Web · Spring Data JPA · Spring AMQP · PostgreSQL 16 · RabbitMQ 4 · Flyway · Docker Compose · GitHub Actions

## Run locally

Requires Docker with Compose.

~~~sh
docker compose up --build
~~~

The API listens on localhost port 8081. RabbitMQ management is available on port 15672 (local development credentials: orders / orders).

Create an order:

~~~sh
curl -X POST http://localhost:8081/api/v1/orders -H 'Content-Type: application/json' -d '{"clientReference":"web-checkout-2027-0001","customerEmail":"alex@example.ie","amount":129.95,"currency":"EUR"}'
~~~

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/v1/orders | Create an order and enqueue OrderCreated |
| GET | /api/v1/orders?page=0&size=20 | List orders |
| GET | /api/v1/orders/{id} | Retrieve one order |
| GET | /actuator/health | Health status |

See requests/orders.http for runnable examples.

## Delivery semantics

The order row and outbox row are committed together. A scheduled publisher sends pending rows and records success or retry state. Delivery is at least once: a process may stop after RabbitMQ accepts a message but before the database records it as published. Consumers should deduplicate with the eventId message identifier. The included audit listener only logs messages.

This demo runs one publisher instance. A horizontally scaled deployment needs an atomic claim or lease mechanism for outbox rows and a consumer-side inbox or deduplication strategy. The service does not claim exactly-once delivery.

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| DB_URL | jdbc:postgresql://localhost:5433/orders | JDBC connection URL |
| DB_USERNAME | orders | Database user |
| DB_PASSWORD | orders | Database password |
| RABBITMQ_HOST | localhost | RabbitMQ host |
| RABBITMQ_PORT | 5672 | RabbitMQ AMQP port |
| RABBITMQ_USERNAME | guest | RabbitMQ user |
| RABBITMQ_PASSWORD | guest | RabbitMQ password |
| OUTBOX_POLL_INTERVAL_MS | 5000 | Publisher polling delay |

Defaults are intended for local development. Use managed credentials outside local use.
