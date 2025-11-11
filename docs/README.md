# E-Health Care Platform

## Overview

The E-Health Care Platform is a comprehensive, production-grade full-stack web application designed to streamline healthcare management for hospitals and clinics. Built with a microservices architecture, it enables efficient handling of patients, doctors, appointments, billing, and notifications. The platform ensures scalability, security, and ease of use through modern technologies.

### Key Features
- **Patient Management**: Complete CRUD operations for patient records with search functionality.
- **Doctor Management**: Profiles, availability, and scheduling for healthcare providers.
- **Appointment Booking**: Seamless scheduling, validation, and status tracking.
- **Billing System**: Automated bill generation and payment tracking.
- **Notifications**: Event-driven notifications via Kafka for real-time updates.
- **Authentication & Authorization**: JWT-based security with role-based access (Admin, Doctor, User).
- **Dashboard**: Overview and statistics for quick insights.
- **Responsive UI**: Modern Angular frontend with Material Design.

### System Architecture Diagram

```
[Angular Frontend (Nginx + Node)]
          |
          | (REST API)
          v
[API Gateway (Spring Cloud Gateway)]
          |
          +-------------------+
          |                   |
          v                   v
[Auth Service]     [Discovery Server (Eureka)]
          |                   |
          +-------------------+
          |                   |
          v                   v
[Patient Service]  [Doctor Service]
          |                   |
          +-------------------+
          |                   |
          v                   v
[Appointment Service] [Billing Service]
          |                   |
          +-------------------+
          |                   |
          v                   v
[Notification Service] [Config Server]
          |
          +-------------------+
          |                   |
          v                   v
[MongoDB]          [Redis Cache]
          |
          +-------------------+
          |                   |
          v                   v
[Kafka Message Broker] [Zookeeper]
```

### Tech Stack
- **Backend**: Java 21, Spring Boot 3.x, Spring Cloud (Eureka, Gateway, Config Server), Maven
- **Database**: MongoDB
- **Cache**: Redis
- **Message Broker**: Apache Kafka
- **Frontend**: Angular 19, TypeScript, SCSS, Angular Material
- **Containerization**: Docker, Docker Compose
- **Security**: JWT, Spring Security
- **Testing**: JUnit 5, Testcontainers, Jasmine + Karma
- **Documentation**: OpenAPI/Swagger

### Getting Started
1. Clone the repository.
2. Follow the setup instructions in `docs/DEVELOPER-GUIDE.md`.
3. Run `make build && make up` to start the full stack locally.

For detailed instructions, refer to `docs/DEPLOYMENT.md` and `docs/DEVELOPER-GUIDE.md`.

### Contributing
Please read `docs/DEVELOPER-GUIDE.md` for development guidelines and `docs/TESTING.md` for testing procedures.

### License
This project is licensed under the MIT License.
