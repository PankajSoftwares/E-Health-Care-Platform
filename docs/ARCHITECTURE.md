# Architecture Documentation

## Tech Stack and Rationale

### Backend Technologies
- **Java 21**: Latest LTS version with modern features like records, pattern matching, and improved performance. Chosen for its robustness, security, and extensive ecosystem in enterprise applications.
- **Spring Boot 3.x**: Provides rapid application development with auto-configuration, embedded servers, and production-ready features. Version 3.x ensures compatibility with Java 21 and latest Spring ecosystem.
- **Spring Cloud**: Enables microservices architecture with Eureka for service discovery, Gateway for API routing, and Config Server for centralized configuration management.
- **Maven**: Build tool for dependency management, compilation, and packaging. Preferred over Gradle for its XML-based configuration which is more predictable in enterprise settings.
- **MongoDB**: NoSQL document database chosen for its flexibility in handling varied healthcare data structures, scalability, and ease of integration with Spring Data MongoDB.
- **Redis**: In-memory data store used for caching to improve performance of frequently accessed data like user sessions and temporary computations.
- **Apache Kafka**: Distributed event streaming platform for asynchronous communication between microservices, ensuring loose coupling and scalability for notification events.

### Frontend Technologies
- **Angular 19**: Latest version of the popular framework for building scalable single-page applications. Provides strong typing with TypeScript, component-based architecture, and excellent tooling.
- **TypeScript**: Enhances JavaScript with static typing, improving code quality and developer experience.
- **SCSS**: CSS preprocessor for better maintainability and advanced styling features.
- **Angular Material**: UI component library for consistent, responsive design following Material Design principles.
- **Angular CLI**: Official tool for Angular development, providing commands for building, testing, and deploying applications.

### Infrastructure and DevOps
- **Docker**: Containerization platform for packaging applications and their dependencies into standardized units. Ensures consistency across development, testing, and production environments.
- **Docker Compose**: Tool for defining and running multi-container Docker applications. Simplifies local development and orchestration of the entire stack.
- **Nginx**: Web server used as reverse proxy for the Angular frontend, providing load balancing, SSL termination, and static file serving.

### Security
- **JWT (JSON Web Tokens)**: Stateless authentication mechanism for secure API communication.
- **Spring Security**: Framework for authentication and authorization in Spring applications.
- **HTTPS**: Secure communication protocol, with self-signed certificates for development and proper certificates for production.

### Testing and Quality Assurance
- **JUnit 5**: Modern testing framework for Java unit tests.
- **Testcontainers**: Library for integration testing with real dependencies in Docker containers.
- **Jasmine + Karma**: Testing framework for Angular applications.
- **Postman**: API testing tool for manual and automated API validation.

## Architecture Principles

### Microservices Design
- **Decomposition**: Each service (Auth, Patient, Doctor, etc.) is independently deployable and scalable.
- **API Gateway**: Single entry point for all client requests, handling routing, authentication, and rate limiting.
- **Service Discovery**: Eureka server allows services to register and discover each other dynamically.
- **Centralized Configuration**: Config Server manages configuration across all services, enabling environment-specific settings.

### Layered Architecture
Each microservice follows a clean layered architecture:
- **Controller Layer**: Handles HTTP requests and responses, input validation.
- **Service Layer**: Contains business logic and orchestrates operations.
- **Repository Layer**: Manages data access and persistence.

### Event-Driven Communication
- Kafka is used for asynchronous communication between services, particularly for notifications and cross-service updates.
- Ensures loose coupling and improves system resilience.

### Security Architecture
- JWT tokens are issued by the Auth Service and validated at the API Gateway.
- Role-based access control (RBAC) with predefined roles: ADMIN, DOCTOR, USER.
- All inter-service communication is secured with appropriate authentication.

### Scalability and Performance
- Horizontal scaling through containerization and orchestration.
- Caching with Redis for improved response times.
- Asynchronous processing with Kafka to handle high loads.

### Observability
- Spring Boot Actuator provides health checks, metrics, and info endpoints for each service.
- Centralized logging and monitoring can be implemented using ELK stack or similar tools.

## Deployment Architecture

### Local Development
- Docker Compose orchestrates all services, databases, and infrastructure components.
- Each service runs in its own container with proper networking.

### Production Deployment
- Containerized services deployed to cloud platforms (AWS ECS, Kubernetes, etc.).
- Load balancers and auto-scaling groups for high availability.
- Separate environments for staging and production.

## Data Flow

1. Client requests come through the Angular frontend.
2. API Gateway validates JWT and routes to appropriate microservice.
3. Microservice processes the request, interacting with MongoDB and Redis as needed.
4. Events are published to Kafka for asynchronous processing (e.g., notifications).
5. Notification Service consumes events and sends notifications.
6. Responses flow back through the same path.

This architecture ensures a scalable, maintainable, and secure healthcare platform that can grow with the organization's needs.
