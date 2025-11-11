# E-Health-Care-Platform Development TODO

## Step 1: Documentation
- [x] Create docs/README.md with overview, features, and system diagram
- [x] Create docs/ARCHITECTURE.md with tech stack and rationale
- [x] Create docs/API-SPEC.md with API endpoints (OpenAPI/Swagger summaries)
- [x] Create docs/DEPLOYMENT.md with deployment guide (Docker, local, and cloud)
- [x] Create docs/DEVELOPER-GUIDE.md with setup instructions and developer commands
- [x] Create docs/TESTING.md with test instructions (Testcontainers, Postman)
- [x] Create docs/CHANGELOG.md (v1.0.0 initial release)

## Step 2: Backend Microservices Implementation
### Config Server
- [x] Create backend/config-server/ with pom.xml, Dockerfile, application.yml
- [x] Implement Controller → Service → Repository layers
- [x] Add DTOs and Validation annotations
- [x] Add Swagger/OpenAPI documentation
- [x] Write JUnit and integration tests
- [x] Add health endpoints

### Discovery Server (Eureka)
- [x] Create backend/discovery-server/ with pom.xml, Dockerfile, application.yml
- [x] Implement service registry
- [x] Add health endpoints and tests

### API Gateway
- [x] Create backend/api-gateway/ with pom.xml, Dockerfile, application.yml
- [x] Implement JWT validation and routing
- [x] Add health endpoints and tests

### Auth Service
- [x] Create backend/auth-service/ with pom.xml, Dockerfile, application.yml
- [x] Implement JWT issuance and role management
- [x] Add Controller → Service → Repository layers
- [x] Add DTOs, Validation, Swagger, tests, health endpoints
- [x] Fix package path for AuthRequest.java to com.ehealth.auth.service.dto
- [x] Fix Lombok and Maven compiler compatibility issues
- [x] Fix AuthServiceApplicationTests to disable Spring Cloud Config and Eureka for testing

### Patient Service
- [x] Create backend/patient-service/ with pom.xml, Dockerfile, application.yml
- [x] Implement CRUD operations for patients
- [x] Add Controller → Service → Repository layers
- [x] Add DTOs, Validation, Swagger, tests, health endpoints

### Doctor Service
- [x] Create backend/doctor-service/ with pom.xml, Dockerfile, application.yml
- [x] Implement doctor profiles and availability
- [x] Add Controller → Service → Repository layers
- [x] Add DTOs, Validation, Swagger, tests, health endpoints

### Appointment Service
- [ ] Create backend/appointment-service/ with pom.xml, Dockerfile, application.yml
- [ ] Implement appointment scheduling and validation
- [ ] Add Kafka Producers/Consumers
- [ ] Add Controller → Service → Repository layers
- [ ] Add DTOs, Validation, Swagger, tests, health endpoints

### Billing Service
- [ ] Create backend/billing-service/ with pom.xml, Dockerfile, application.yml
- [ ] Implement bill generation and payment tracking
- [ ] Add Kafka Producers/Consumers
- [ ] Add Controller → Service → Repository layers
- [ ] Add DTOs, Validation, Swagger, tests, health endpoints

### Notification Service
- [ ] Create backend/notification-service/ with pom.xml, Dockerfile, application.yml
- [ ] Implement Kafka-based event notifications
- [ ] Add REST endpoints for notifications
- [ ] Add Controller → Service → Repository layers
- [ ] Add DTOs, Validation, Swagger, tests, health endpoints

### Shared Libraries
- [ ] Create backend/libs/ with shared DTOs and common utils

## Step 3: Frontend Implementation
- [ ] Create frontend/ehealth-angular-app/ Angular 19 project
- [ ] Implement Auth module (Login/Register with JWT)
- [ ] Implement Dashboard module (Overview & Stats)
- [ ] Implement Patient Management module (CRUD + Search)
- [ ] Implement Doctor Management module
- [ ] Implement Appointment Booking & Status Tracking module
- [ ] Implement Billing Overview module
- [ ] Implement Notifications Center module
- [ ] Use Angular Material for responsive UI
- [ ] Integrate REST APIs via environment.ts
- [ ] Implement JWT token storage and route guards
- [ ] Add Dockerfile for frontend
- [ ] Add unit tests with Jasmine + Karma

## Step 4: Infrastructure & Orchestration
- [ ] Create backend/docker-compose.yml with all services
- [ ] Create top-level docker-compose.yml for full stack
- [ ] Create Makefile with targets: build, up, down, test, seed
- [ ] Create scripts/build-all.sh
- [ ] Create scripts/start-local.sh
- [ ] Create scripts/stop-local.sh
- [ ] Create scripts/test-all.sh
- [ ] Create scripts/seed-data.sh
- [ ] Create .env.example

## Step 5: Security
- [ ] Implement Spring Security + JWT in all services
- [ ] Define roles: ROLE_ADMIN, ROLE_DOCTOR, ROLE_USER
- [ ] Secure API Gateway and microservice endpoints
- [ ] Configure HTTPS ready (self-signed for dev)
- [ ] Store secrets in Config Server or env vars

## Step 6: Testing & Quality Assurance
- [ ] Ensure JUnit 5 unit tests for all services
- [ ] Use Testcontainers for integration tests
- [ ] Provide Postman collection
- [ ] Achieve ≥80% test coverage
- [ ] Create CI/CD template in /ci (optional)

## Step 7: Deployment
- [ ] Ensure local deployment with docker-compose
- [ ] Provide cloud deployment example (ECS/Kubernetes optional)
- [ ] Update DEPLOYMENT.md with production instructions

## Final Checks
- [ ] Verify all services run with make build && make up
- [ ] Ensure all tests pass
- [ ] Validate API integrations
- [ ] Check frontend-backend communication
