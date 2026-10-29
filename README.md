# Future_PAY

Future_PAY is a Spring Boot demonstration of an **offline UPI payment mesh**. It simulates how encrypted payment packets can move from phone to phone without an active internet connection until a device with connectivity uploads them to the backend.

The project demonstrates offline payment flow, mesh gossip, bridge-node synchronization, encrypted payment packets, transaction settlement, and duplicate protection through idempotency handling.

## Features

- Offline payment packet creation
- Device-to-device mesh gossip simulation
- Bridge nodes that upload packets when internet connectivity is available
- Hybrid encryption using:
  - RSA-2048 with OAEP-SHA256
  - AES-256-GCM for payment data
- Idempotent transaction ingestion
- Replay and packet freshness protection
- H2 in-memory database
- Thymeleaf web dashboard
- REST API for payment, mesh, account, and transaction operations
- Concurrent bridge uploads to simulate duplicate packet delivery
- Live transaction and device status monitoring

## Technology Stack

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Thymeleaf
- H2 Database
- Maven
- HTML, CSS, and JavaScript

## Project Structure

```text
src/
├── main/
│   ├── java/com/example/UPI/
│   │   ├── config/       # Application configuration
│   │   ├── controller/   # REST and dashboard controllers
│   │   ├── crypto/       # Encryption and key management
│   │   ├── model/        # Entities, DTOs, and repositories
│   │   └── service/      # Payment, mesh, bridge, and settlement logic
│   └── resources/
│       ├── templates/
│       │   └── dashboard.html
│       └── application.properties
└── test/
    └── java/              # Application tests
```

## How the Demo Works

The application simulates the following flow:

1. A sender creates a payment.
2. The payment is encrypted into a mesh packet.
3. The packet is injected into an offline device.
4. Devices exchange packets through mesh gossip.
5. A bridge device with internet connectivity collects the packets.
6. Bridge devices upload packets to the backend.
7. The backend validates, deduplicates, and settles the transaction.
8. Account balances and transaction results are displayed on the dashboard.

## Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 25 or later
- Git
- Maven, or use the included Maven Wrapper

### Clone the Repository

```bash
git clone https://github.com/TheBhowmik/Future_PAY.git
cd Future_PAY
```

### Run the Application

Using the Maven Wrapper on Linux or macOS:

```bash
./mvnw spring-boot:run
```

Using the Maven Wrapper on Windows:

```powershell
mvnw.cmd spring-boot:run
```

Alternatively, if Maven is installed globally:

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8081
```

Open the dashboard in your browser:

```text
http://localhost:8081/
```

## Dashboard Demo

The dashboard allows you to:

- Select a sender and receiver
- Enter a payment amount and PIN
- Inject an encrypted packet into the mesh
- Run a gossip round
- Upload packets from bridge devices
- Reset the mesh and idempotency cache
- View device connectivity and packet counts
- View account balances
- View the transaction ledger
- Monitor real-time activity logs

## REST API

### Get Server Public Key

```http
GET /api/server-key
```

Returns the server's public key and encryption scheme.

### Create and Inject a Demo Payment

```http
POST /api/demo/send
Content-Type: application/json
```

Example request:

```json
{
  "senderVpa": "alice@demo",
  "receiverVpa": "bob@demo",
  "amount": 500,
  "pin": "1234",
  "ttl": 5,
  "startDevice": "phone-alice"
}
```

### View Mesh State

```http
GET /api/mesh/state
```

Returns the connected devices, packet counts, packet IDs, and idempotency cache size.

### Run a Gossip Round

```http
POST /api/mesh/gossip
```

Moves packets between simulated devices.

### Flush Bridge Devices

```http
POST /api/mesh/flush
```

Uploads packets from bridge devices to the backend. Uploads are performed in parallel to test duplicate handling.

### Reset the Mesh

```http
POST /api/mesh/reset
```

Clears the simulated mesh and idempotency cache.

### Ingest a Packet from a Bridge Node

```http
POST /api/bridge/ingest
Content-Type: application/json
X-Bridge-Node-Id: bridge-1
X-Hop-Count: 3
```

This represents the production-style endpoint that a real bridge device would use.

### List Accounts

```http
GET /api/accounts
```

### List Recent Transactions

```http
GET /api/transactions
```

## Database

Future_PAY uses an H2 in-memory database by default.

Database configuration:

```text
JDBC URL: jdbc:h2:mem:upimesh
Username: sa
Password: empty
```

The H2 console is available at:

```text
http://localhost:8081/h2-console
```

Use the following connection settings:

```text
JDBC URL: jdbc:h2:mem:upimesh
User Name: sa
Password:
```

The database is recreated whenever the application restarts.

## Configuration

Application settings are located in:

```text
src/main/resources/application.properties
```

Important configuration options include:

```properties
server.port=8081
upi.mesh.idempotency-ttl-seconds=86400
upi.mesh.packet-max-age-seconds=86400
```

- `server.port` controls the application port.
- `upi.mesh.idempotency-ttl-seconds` controls how long processed packet hashes are remembered.
- `upi.mesh.packet-max-age-seconds` controls the maximum allowed packet age.

## Testing

Run the test suite with:

```bash
./mvnw test
```

On Windows:

```powershell
mvnw.cmd test
```

## Building the Application

Create a packaged JAR file:

```bash
./mvnw clean package
```

Run the generated JAR:

```bash
java -jar target/*.jar
```

## Security Notes

This project is intended as a demonstration and simulation. It includes cryptographic mechanisms and idempotency protections for educational purposes, but it should not be treated as production-ready payment infrastructure without additional security reviews, secure key storage, authentication, authorization, monitoring, and compliance controls.

## Future Improvements

Potential enhancements include:

- Persistent production database support
- User authentication and authorization
- Real mobile mesh networking
- Secure hardware-backed key storage
- Digital signatures for payment packets
- Improved packet routing and delivery guarantees
- Docker support
- Integration and end-to-end tests
- Production-grade observability and audit logging

## License

No license has been specified for this repository yet.
