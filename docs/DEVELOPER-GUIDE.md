# Developer Guide

This guide provides instructions for developers working on the E-Health Care Platform.

## Getting Started

### Prerequisites

- Java 21 JDK
- Maven 3.8+
- Node.js 18+ and npm
- Docker and Docker Compose
- Git
- IDE (IntelliJ IDEA, VS Code, or Eclipse)

### Clone and Setup

```bash
git clone <repository-url>
cd e-healthcare-platform

# Copy environment file
cp .env.example .env

# Make scripts executable
chmod +x scripts/*.sh
```

## Development Workflow

### 1. Start Infrastructure

```bash
# Start databases and message broker
docker-compose -f backend/docker-compose.yml up -d mongodb redis kafka zookeeper
```

### 2. Start Backend Services

Start services in this order:

```bash
# Config Server (first)
mvn -pl backend/config-server spring-boot:run -Dspring-boot.run.profiles=dev

# Discovery Server
mvn -pl backend/discovery-server spring-boot:run -Dspring-boot.run.profiles=dev

# API Gateway
mvn -pl backend/api-gateway spring-boot:run -Dspring-boot.run.profiles=dev

# Auth Service
mvn -pl backend/auth-service spring-boot:run -Dspring-boot.run.profiles=dev

# Other services (can be started in any order)
mvn -pl backend/patient-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl backend/doctor-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl backend/appointment-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl backend/billing-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl backend/notification-service spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Start Frontend

```bash
cd frontend/ehealth-angular-app
npm install
npm start
```

### 4. Access Applications

- Frontend: http://localhost:4200 (Angular dev server)
- API Gateway: http://localhost:8080
- Swagger UI: http://localhost:8081/swagger-ui.html (Patient Service example)
- Eureka Dashboard: http://localhost:8761

## Project Structure

```
e-healthcare-platform/
├── backend/
│   ├── config-server/          # Centralized configuration
│   ├── discovery-server/       # Service registry (Eureka)
│   ├── api-gateway/           # API routing and authentication
│   ├── auth-service/          # JWT authentication
│   ├── patient-service/       # Patient management
│   ├── doctor-service/        # Doctor profiles
│   ├── appointment-service/   # Appointment scheduling
│   ├── billing-service/       # Billing and payments
│   ├── notification-service/  # Event notifications
│   ├── libs/                  # Shared libraries
│   └── docker-compose.yml     # Backend services orchestration
├── frontend/
│   └── ehealth-angular-app/   # Angular application
├── docs/                      # Documentation
├── scripts/                   # Build and deployment scripts
└── Makefile                   # Common tasks
```

## Backend Development

### Service Structure

Each microservice follows this structure:

```
service-name/
├── src/main/java/com/ehealth/servicename/
│   ├── config/                # Configuration classes
│   ├── controller/            # REST controllers
│   ├── service/               # Business logic
│   ├── repository/            # Data access
│   ├── dto/                   # Data transfer objects
│   ├── exception/             # Custom exceptions
│   └── model/                 # Domain models
├── src/main/resources/
│   ├── application.yml        # Application configuration
│   └── bootstrap.yml          # Bootstrap configuration
├── src/test/                  # Unit and integration tests
├── Dockerfile                 # Container definition
└── pom.xml                    # Maven configuration
```

### Key Development Guidelines

#### 1. Code Style

- Follow Java naming conventions
- Use Lombok for boilerplate code (getters, setters, constructors)
- Use records for immutable DTOs (Java 14+)
- Maintain consistent package structure: `com.ehealth.<servicename>`

#### 2. Layered Architecture

- **Controller**: Handle HTTP requests, validate input, return responses
- **Service**: Implement business logic, orchestrate operations
- **Repository**: Data access layer, interact with database

#### 3. Error Handling

- Use custom exception classes extending `RuntimeException`
- Implement `@ControllerAdvice` for global exception handling
- Return appropriate HTTP status codes

#### 4. Validation

- Use Bean Validation annotations (`@NotNull`, `@Size`, etc.)
- Validate input in controllers and services

#### 5. Security

- All endpoints protected by JWT (except auth endpoints)
- Use `@PreAuthorize` for method-level security
- Validate user roles and permissions

#### 6. Testing

- Unit tests for service and repository layers
- Integration tests using Testcontainers
- Mock external dependencies

### Adding a New Microservice

1. **Create service directory**
   ```bash
   mkdir backend/new-service
   cd backend/new-service
   ```

2. **Initialize Maven project**
   ```bash
   mvn archetype:generate -DgroupId=com.ehealth -DartifactId=new-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
   ```

3. **Add Spring Boot dependencies to pom.xml**
   ```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>3.1.0</version>
   </parent>

   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <!-- Add other dependencies as needed -->
   </dependencies>
   ```

4. **Create application.yml and bootstrap.yml**

5. **Implement service structure** (controller, service, repository)

6. **Add to docker-compose.yml**

7. **Register with Eureka** (add @EnableDiscoveryClient)

## Frontend Development

### Angular Project Structure

```
frontend/ehealth-angular-app/
├── src/
│   ├── app/
│   │   ├── core/               # Core services (auth, interceptors)
│   │   ├── shared/             # Shared components and utilities
│   │   ├── features/           # Feature modules
│   │   │   ├── auth/           # Authentication module
│   │   │   ├── dashboard/      # Dashboard module
│   │   │   ├── patients/       # Patient management
│   │   │   ├── doctors/        # Doctor management
│   │   │   ├── appointments/   # Appointment booking
│   │   │   ├── billing/        # Billing overview
│   │   │   └── notifications/  # Notifications center
│   │   └── app-routing.module.ts
│   ├── assets/                 # Static assets
│   ├── environments/           # Environment configurations
│   └── styles.scss             # Global styles
├── Dockerfile                  # Frontend container
└── angular.json                # Angular CLI config
```

### Key Development Guidelines

#### 1. Module Structure

- Use feature modules for better organization
- Lazy load feature modules for better performance
- Keep core module for singleton services

#### 2. State Management

- Use Angular services for state management
- Consider NgRx for complex state scenarios

#### 3. HTTP Client

- Use HttpClient for API calls
- Implement interceptors for JWT tokens and error handling
- Handle loading states and error responses

#### 4. Forms

- Use reactive forms for complex forms
- Implement validation and error handling
- Use Angular Material form components

#### 5. Authentication

- Implement JWT token storage (localStorage/sessionStorage)
- Create auth guards for route protection
- Handle token refresh automatically

#### 6. UI/UX

- Use Angular Material for consistent design
- Implement responsive design
- Follow accessibility guidelines

### Running Frontend Tests

```bash
cd frontend/ehealth-angular-app
npm test          # Unit tests
npm run test:ci   # CI mode
npm run e2e       # End-to-end tests
```

## Testing

### Backend Testing

#### Unit Tests
```java
@SpringBootTest
class PatientServiceTest {
    @Test
    void shouldCreatePatient() {
        // Test implementation
    }
}
```

#### Integration Tests with Testcontainers
```java
@Testcontainers
@SpringBootTest
class PatientServiceIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Test
    void shouldSavePatientToDatabase() {
        // Test implementation
    }
}
```

### API Testing with Postman

Import the provided Postman collection (`docs/postman/EHealthCare.postman_collection.json`) and run tests.

## Debugging

### Backend Debugging

1. **Enable debug mode**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev -Ddebug=true
   ```

2. **Remote debugging**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
   ```

3. **Check logs**
   ```bash
   docker-compose logs -f <service-name>
   ```

### Frontend Debugging

1. **Browser developer tools**
   - Use Chrome DevTools for debugging
   - Check Network tab for API calls
   - Use Console for logging

2. **Angular DevTools**
   - Install Angular DevTools extension
   - Debug component state and change detection

## Code Quality

### Linting and Formatting

#### Backend
```bash
# Check style
mvn checkstyle:check

# Format code
mvn formatter:format
```

#### Frontend
```bash
# Lint
npm run lint

# Format
npm run format
```

### Code Coverage

```bash
# Backend coverage
mvn test jacoco:report

# Frontend coverage
npm run test:coverage
```

## Git Workflow

1. **Create feature branch**
   ```bash
   git checkout -b feature/new-feature
   ```

2. **Make changes and commit**
   ```bash
   git add .
   git commit -m "Add new feature"
   ```

3. **Push and create PR**
   ```bash
   git push origin feature/new-feature
   ```

4. **Code review and merge**

### Commit Message Convention

```
type(scope): description

Types: feat, fix, docs, style, refactor, test, chore
```

## CI/CD

### GitHub Actions (Example)

```yaml
name: CI/CD Pipeline
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run tests
        run: mvn test
```

## Common Commands

### Build Everything
```bash
./scripts/build-all.sh
# or
make build
```

### Start Full Stack
```bash
./scripts/start-local.sh
# or
make up
```

### Stop Services
```bash
./scripts/stop-local.sh
# or
make down
```

### Run Tests
```bash
./scripts/test-all.sh
# or
make test
```

### Seed Data
```bash
./scripts/seed-data.sh
# or
make seed
```

### Run Specific Service
```bash
mvn -pl backend/patient-service spring-boot:run -Dspring-boot.run.profiles=dev
```

### Clean Build
```bash
mvn clean install
npm run clean
```

## Troubleshooting

### Common Issues

1. **Port already in use**
   - Find process: `lsof -i :8080`
   - Kill process: `kill -9 <PID>`

2. **Service not registering with Eureka**
   - Check bootstrap.yml configuration
   - Ensure Eureka server is running first

3. **Database connection issues**
   - Verify Docker containers are running
   - Check connection strings in application.yml

4. **CORS errors**
   - Configure CORS in API Gateway
   - Check allowed origins in configuration

5. **JWT token issues**
   - Verify token format and expiration
   - Check JWT secret configuration

### Getting Help

- Check existing issues in the repository
- Review documentation in `docs/`
- Contact the development team

## Contributing

1. Follow the coding standards and guidelines
2. Write tests for new features
3. Update documentation as needed
4. Ensure all tests pass before submitting PR
5. Follow the Git workflow for contributions

Happy coding! 🚀
