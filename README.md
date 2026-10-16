# NammaArena - Auth Service

Authentication and Authorization Service for the NammaArena microservices architecture. Handles user registration, login and JWT token generation.

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security
- JWT (jjwt 0.11.5)
- Spring Data JPA
- MySQL

## Port
`8081`

## Getting Started

### Prerequisites
- Java 17
- Maven 3.9+
- MySQL running on port 3306
- Eureka Server running on port 8761

### Run Locally
```bash
mvn clean package -DskipTests
java -jar target/*.jar
```

### Run with Docker
```bash
docker build -t nammaarena-auth-service .
docker run -p 8081:8081 nammaarena-auth-service
```

## API Endpoints
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |
| GET | `/api/auth/users` | Get all users and owners | Admin only |
| DELETE | `/api/auth/users/{userId}` | Delete a user | Admin only |

## Roles
- `USER` — Regular user who can book turfs
- `OWNER` — Turf owner who can manage turfs
- `ADMIN` — Admin who can manage users

## Part of NammaArena Microservices
| Service | Repository |
|---|---|
| Eureka Server | [NammaArena-EurekaServer](https://github.com/VamsiK-11/NammaArena-EurekaServer) |
| API Gateway | [NammaArena-APIGateway](https://github.com/VamsiK-11/NammaArena-APIGateway) |
| Auth Service | [NammaArena-AuthService](https://github.com/VamsiK-11/NammaArena-AuthService) |
| Turf Service | [NammaArena-TurfService](https://github.com/VamsiK-11/NammaArena-TurfService) |
| Booking Service | [NammaArena-BookingService](https://github.com/VamsiK-11/NammaArena-BookingService) |
| Payment Service | [NammaArena-PaymentService](https://github.com/VamsiK-11/NammaArena-PaymentService) |
