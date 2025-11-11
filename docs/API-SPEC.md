# API Specification

This document outlines the REST API endpoints for the E-Health Care Platform. All APIs follow RESTful conventions and use JSON for request/response bodies. Authentication is handled via JWT tokens in the Authorization header.

## Authentication

All protected endpoints require a valid JWT token: `Authorization: Bearer <token>`

### Auth Service (Port: 8082)
- **POST /api/v1/auth/login** - User login
  - Body: `{ "username": "string", "password": "string" }`
  - Response: `{ "token": "string", "expiresIn": "number", "user": {...} }`

- **POST /api/v1/auth/register** - User registration
  - Body: `{ "username": "string", "email": "string", "password": "string", "role": "USER|DOCTOR|ADMIN" }`
  - Response: `{ "message": "User registered successfully" }`

- **POST /api/v1/auth/refresh** - Refresh JWT token
  - Headers: Authorization
  - Response: `{ "token": "string", "expiresIn": "number" }`

### Patient Service (Port: 8081)
- **GET /api/v1/patients** - Get all patients (paginated)
  - Query: `?page=0&size=10&search=string`
  - Response: `{ "content": [...], "totalElements": number, "totalPages": number }`

- **GET /api/v1/patients/{id}** - Get patient by ID
  - Response: Patient object

- **POST /api/v1/patients** - Create new patient
  - Body: Patient DTO
  - Response: Created Patient object

- **PUT /api/v1/patients/{id}** - Update patient
  - Body: Patient DTO
  - Response: Updated Patient object

- **DELETE /api/v1/patients/{id}** - Delete patient
  - Response: `{ "message": "Patient deleted successfully" }`

### Doctor Service (Port: 8086)
- **GET /api/v1/doctors** - Get all doctors
  - Query: `?specialty=string&available=true`
  - Response: Array of Doctor objects

- **GET /api/v1/doctors/{id}** - Get doctor by ID
  - Response: Doctor object

- **POST /api/v1/doctors** - Create new doctor
  - Body: Doctor DTO
  - Response: Created Doctor object

- **PUT /api/v1/doctors/{id}** - Update doctor
  - Body: Doctor DTO
  - Response: Updated Doctor object

- **DELETE /api/v1/doctors/{id}** - Delete doctor
  - Response: `{ "message": "Doctor deleted successfully" }`

### Appointment Service (Port: 8083)
- **GET /api/v1/appointments** - Get appointments
  - Query: `?patientId=uuid&doctorId=uuid&status=PENDING|CONFIRMED|CANCELLED&date=yyyy-MM-dd`
  - Response: Array of Appointment objects

- **GET /api/v1/appointments/{id}** - Get appointment by ID
  - Response: Appointment object

- **POST /api/v1/appointments** - Book new appointment
  - Body: Appointment DTO
  - Response: Created Appointment object

- **PUT /api/v1/appointments/{id}/status** - Update appointment status
  - Body: `{ "status": "CONFIRMED|CANCELLED" }`
  - Response: Updated Appointment object

- **DELETE /api/v1/appointments/{id}** - Cancel appointment
  - Response: `{ "message": "Appointment cancelled successfully" }`

### Billing Service (Port: 8084)
- **GET /api/v1/bills** - Get bills
  - Query: `?patientId=uuid&status=PAID|UNPAID`
  - Response: Array of Bill objects

- **GET /api/v1/bills/{id}** - Get bill by ID
  - Response: Bill object

- **POST /api/v1/bills** - Generate new bill
  - Body: Bill DTO
  - Response: Created Bill object

- **PUT /api/v1/bills/{id}/pay** - Mark bill as paid
  - Body: `{ "paymentMethod": "CREDIT_CARD|CASH|INSURANCE" }`
  - Response: Updated Bill object

### Notification Service (Port: 8085)
- **GET /api/v1/notifications** - Get user notifications
  - Query: `?userId=uuid&read=false`
  - Response: Array of Notification objects

- **PUT /api/v1/notifications/{id}/read** - Mark notification as read
  - Response: Updated Notification object

## Common Response Formats

### Success Response
```json
{
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2023-01-01T00:00:00Z"
}
```

### Error Response
```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "timestamp": "2023-01-01T00:00:00Z"
}
```

## Data Transfer Objects (DTOs)

### Patient DTO
```json
{
  "id": "uuid",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "dateOfBirth": "date",
  "address": {
    "street": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string"
  },
  "medicalHistory": "string",
  "emergencyContact": {
    "name": "string",
    "phone": "string",
    "relationship": "string"
  }
}
```

### Doctor DTO
```json
{
  "id": "uuid",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "specialty": "string",
  "licenseNumber": "string",
  "available": true,
  "schedule": [
    {
      "dayOfWeek": "MONDAY",
      "startTime": "09:00",
      "endTime": "17:00"
    }
  ]
}
```

### Appointment DTO
```json
{
  "id": "uuid",
  "patientId": "uuid",
  "doctorId": "uuid",
  "appointmentDate": "datetime",
  "duration": 30,
  "status": "PENDING",
  "notes": "string",
  "type": "CONSULTATION|FOLLOW_UP"
}
```

### Bill DTO
```json
{
  "id": "uuid",
  "patientId": "uuid",
  "appointmentId": "uuid",
  "amount": 100.00,
  "currency": "USD",
  "status": "UNPAID",
  "dueDate": "date",
  "items": [
    {
      "description": "Consultation",
      "quantity": 1,
      "unitPrice": 100.00
    }
  ]
}
```

### Notification DTO
```json
{
  "id": "uuid",
  "userId": "uuid",
  "type": "APPOINTMENT_REMINDER|BILL_DUE",
  "title": "string",
  "message": "string",
  "read": false,
  "createdAt": "datetime"
}
```

## OpenAPI/Swagger Documentation

Each microservice exposes Swagger UI at `/swagger-ui.html` and OpenAPI spec at `/v3/api-docs`.

## Rate Limiting

- Auth endpoints: 10 requests per minute per IP
- Other endpoints: 100 requests per minute per user

## Versioning

All APIs are versioned with `/api/v1/` prefix. Future versions will use `/api/v2/`, etc.

## Pagination

List endpoints support pagination with `page` and `size` query parameters. Default page size is 20.

## Validation

All input data is validated using Bean Validation annotations. Invalid requests return 400 Bad Request with detailed error messages.
