
# Future_PAY

Future_PAY is a Spring Boot demonstration of an offline UPI payment mesh. It simulates how encrypted payment packets can move from phone to phone without an active internet connection until a device with connectivity uploads them to a production-grade backend.

The project demonstrates offline payment flow, mesh gossip, bridge-node synchronization, hybrid cryptographic packet encryption, transaction settlement, and distributed duplicate protection through Redis idempotency handling.

## Features

- **Offline Payment Packet Creation:** Simulates device-level packet generation and signing.
- **Mesh Gossip Simulation:** Device-to-device packet forwarding with Time-to-Live (TTL) routing.
- **Concurrent Bridge Uploads:** Simulates internet-connected devices flushing packets to the backend simultaneously to test race conditions.
- **Hybrid Cryptography:**
  - RSA-2048 with OAEP-SHA256 for session keys.
  - AES-256-GCM for authenticated payment data encryption.
- **Distributed Idempotency:** Redis-backed caching ensures duplicate packets from multiple bridge nodes are rejected across load-balanced clusters.
- **Production Persistence:** PostgreSQL ledger with Flyway database migrations and JPA Auditing.
- **Observability & Security:** Spring Security authentication and Prometheus/Actuator metrics.
- **Environment Profiles:** Switch between local development (H2) and production infrastructure (PostgreSQL, Redis, Docker).
- **Interactive Dashboard:** Thymeleaf web interface for monitoring transactions, device status, and mesh state.

## Technology Stack

- **Core:** Java 25, Spring Boot 4.1.1
- **Web & Security:** Spring Web MVC, Spring Security, Thymeleaf
- **Data & Persistence:** Spring Data JPA, PostgreSQL, Flyway, H2 (Local Profile)
- **Distributed Caching:** Redis, Spring Data Redis
- **Observability:** Spring Boot Actuator, Micrometer (Prometheus Registry)
- **Infrastructure:** Docker, Docker Compose, Maven

## Project Structure

```text
.
├── docker-compose.yml
├── Dockerfile
├── src/
│   ├── main/
│   │   ├── java/com/example/UPI/
│   │   │   ├── config/      # App, Security, and JPA configuration
│   │   │   ├── controller/  # REST endpoints and dashboard routes
│   │   │   ├── crypto/      # Hybrid encryption and persistent key management
│   │   │   ├── model/       # JPA Entities, DTOs, and repositories
│   │   │   └── service/     # Mesh simulation, ingestion, idempotency, and settlement
│   │   └── resources/
│   │       ├── db/migration/
│   │       ├── templates/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
└── pom.xml
```

## How the Architecture Works

1. A sender creates a payment that is encrypted into a mesh packet (AES-256-GCM + RSA).
2. The packet is injected into an offline device.
3. Devices exchange packets through Bluetooth-simulated mesh gossip.
4. Bridge devices with internet connectivity collect the packets.
5. Bridge devices upload packets to the `/api/bridge/ingest` endpoint.
6. The backend checks distributed Redis idempotency protection to prevent duplicate processing.
7. Valid, fresh packets are decrypted, validated, and settled in PostgreSQL within a safe JPA transaction.

## Getting Started

### Prerequisites

- Java 25 or later
- Git
- Docker and Docker Compose (for production mode)

### Clone the Repository

```bash
git clone https://github.com/TheBhowmik/Future_PAY.git
cd Future_PAY
```

### Option A: Local Development Mode

Runs the application using an in-memory H2 database and local application state.

**Linux/macOS**

```bash
./mvnw spring-boot:run
```

**Windows**

```bat
mvnw.cmd spring-boot:run
```

### Option B: Production Infrastructure Mode (Docker)

Starts PostgreSQL, Redis, and the Spring Boot application.

**1. Build the application JAR**

```bash
./mvnw clean package
```

**2. Start the infrastructure**

```bash
docker compose up --build -d
```

## Dashboard & Endpoints

Access the dashboard in your browser:

- **URL:** http://localhost:8081/
- **Username:** `admin`
- **Password:** `admin123`

### REST API Highlights

- `GET /api/server-key` — Returns the server's public key for client-side encryption.
- `POST /api/demo/send` — Injects an encrypted packet into a specific node.
- `POST /api/mesh/gossip` — Executes one hop of the mesh network.
- `POST /api/mesh/flush` — Parallel-uploads packets from bridge nodes to test Redis idempotency.
- `POST /api/bridge/ingest` — Ingestion endpoint requiring `X-Bridge-Node-Id`.

### Observability

When running in production mode:

- **Health:** http://localhost:8081/actuator/health
- **Metrics:** http://localhost:8081/actuator/prometheus


