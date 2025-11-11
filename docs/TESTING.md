# Testing Guide

This guide covers testing strategies and procedures for the E-Health Care Platform.

## Testing Strategy

### Testing Pyramid

```
End-to-End Tests (E2E)
    Integration Tests
       Unit Tests
```

### Test Types

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test interactions between components
- **End-to-End Tests**: Test complete user workflows
- **Performance Tests**: Test system performance under load
- **Security Tests**: Test authentication and authorization

## Backend Testing

### Unit Testing

#### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mongodb</artifactId>
    <scope>test</scope>
</dependency>
```

#### Unit Test Example

```java
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldCreatePatient() {
        // Given
        PatientDTO patientDTO = createTestPatientDTO();
        Patient patient = createTestPatient();
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        Patient result = patientService.createPatient(patientDTO);

        // Then
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(patientRepository).save(any(Patient.class));
    }
}
```

#### Repository Testing

```java
@DataMongoTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldFindPatientByEmail() {
        // Given
        Patient patient = createTestPatient();
        patientRepository.save(patient);

        // When
        Optional<Patient> found = patientRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }
}
```

### Integration Testing with Testcontainers

#### Integration Test Example

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PatientControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017);

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateAndRetrievePatient() {
        // Given
        PatientDTO patientDTO = createTestPatientDTO();

        // When
        ResponseEntity<PatientDTO> createResponse = restTemplate.postForEntity(
                "/api/v1/patients", patientDTO, PatientDTO.class);
        ResponseEntity<PatientDTO> getResponse = restTemplate.getForEntity(
                "/api/v1/patients/" + createResponse.getBody().getId(), PatientDTO.class);

        // Then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getFirstName()).isEqualTo("John");
    }
}
```

### Controller Testing

```java
@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    void shouldReturnPatientWhenExists() throws Exception {
        // Given
        PatientDTO patientDTO = createTestPatientDTO();
        when(patientService.getPatientById("123")).thenReturn(patientDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/patients/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}
```

## Frontend Testing

### Unit Testing

#### Component Testing

```typescript
describe('PatientListComponent', () => {
  let component: PatientListComponent;
  let fixture: ComponentFixture<PatientListComponent>;
  let patientService: jasmine.SpyObj<PatientService>;

  beforeEach(async () => {
    const patientServiceSpy = jasmine.createSpyObj('PatientService', ['getPatients']);

    await TestBed.configureTestingModule({
      declarations: [PatientListComponent],
      providers: [
        { provide: PatientService, useValue: patientServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PatientListComponent);
    component = fixture.componentInstance;
    patientService = TestBed.inject(PatientService) as jasmine.SpyObj<PatientService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load patients on init', () => {
    const mockPatients = [/* mock data */];
    patientService.getPatients.and.returnValue(of(mockPatients));

    component.ngOnInit();

    expect(patientService.getPatients).toHaveBeenCalled();
    expect(component.patients).toEqual(mockPatients);
  });
});
```

#### Service Testing

```typescript
describe('PatientService', () => {
  let service: PatientService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PatientService]
    });

    service = TestBed.inject(PatientService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should retrieve patients', () => {
    const mockPatients = [/* mock data */];

    service.getPatients().subscribe(patients => {
      expect(patients).toEqual(mockPatients);
    });

    const req = httpMock.expectOne('/api/v1/patients');
    expect(req.request.method).toBe('GET');
    req.flush(mockPatients);
  });
});
```

### End-to-End Testing

#### E2E Test Example

```typescript
describe('Patient Management', () => {
  beforeEach(() => {
    cy.visit('/patients');
  });

  it('should display patient list', () => {
    cy.contains('Patient List');
    cy.get('[data-cy=patient-card]').should('have.length.greaterThan', 0);
  });

  it('should create new patient', () => {
    cy.get('[data-cy=add-patient-btn]').click();
    cy.get('[data-cy=first-name-input]').type('John');
    cy.get('[data-cy=last-name-input]').type('Doe');
    cy.get('[data-cy=email-input]').type('john.doe@example.com');
    cy.get('[data-cy=submit-btn]').click();
    cy.contains('Patient created successfully');
  });

  it('should search patients', () => {
    cy.get('[data-cy=search-input]').type('John');
    cy.get('[data-cy=patient-card]').should('contain', 'John');
  });
});
```

## API Testing with Postman

### Postman Collection Structure

```
E-Health Care Platform
├── Auth
│   ├── Login
│   ├── Register
│   └── Refresh Token
├── Patients
│   ├── Get All Patients
│   ├── Get Patient by ID
│   ├── Create Patient
│   ├── Update Patient
│   └── Delete Patient
├── Doctors
│   ├── Get All Doctors
│   ├── Get Doctor by ID
│   ├── Create Doctor
│   ├── Update Doctor
│   └── Delete Doctor
├── Appointments
│   ├── Get Appointments
│   ├── Get Appointment by ID
│   ├── Book Appointment
│   ├── Update Appointment Status
│   └── Cancel Appointment
├── Billing
│   ├── Get Bills
│   ├── Get Bill by ID
│   ├── Generate Bill
│   └── Mark Bill as Paid
└── Notifications
    ├── Get Notifications
    └── Mark as Read
```

### Environment Variables

```json
{
  "baseUrl": "http://localhost:8080",
  "token": "",
  "patientId": "",
  "doctorId": "",
  "appointmentId": "",
  "billId": ""
}
```

### Test Scripts

```javascript
// Login test
pm.test("Login successful", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.not.be.undefined;
    pm.environment.set("token", jsonData.token);
});

// Set Authorization header
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get("token")
});
```

## Test Data Management

### Test Data Setup

```java
@Component
public class TestDataLoader implements CommandLineRunner {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public void run(String... args) throws Exception {
        if (patientRepository.count() == 0) {
            createTestPatients();
        }
    }

    private void createTestPatients() {
        // Create test patients
    }
}
```

### Seed Script

```bash
#!/bin/bash
# scripts/seed-data.sh

echo "Seeding test data..."

# Seed patients
curl -X POST http://localhost:8081/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890"
  }'

# Seed doctors
curl -X POST http://localhost:8086/api/v1/doctors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Dr. Jane",
    "lastName": "Smith",
    "email": "jane.smith@hospital.com",
    "specialty": "Cardiology"
  }'

echo "Test data seeded successfully!"
```

## Running Tests

### Backend Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PatientServiceTest

# Run with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dgroups=integration
```

### Frontend Tests

```bash
# Run unit tests
npm test

# Run with coverage
npm run test:coverage

# Run E2E tests
npm run e2e

# Run E2E tests headlessly
npm run e2e:ci
```

### Full Test Suite

```bash
# Run all backend tests
./scripts/test-all.sh

# Or using Makefile
make test
```

## Test Coverage

### Backend Coverage

- **Target**: ≥80% code coverage
- **Tools**: JaCoCo
- **Report**: `target/site/jacoco/index.html`

### Frontend Coverage

- **Target**: ≥80% code coverage
- **Tools**: Istanbul (via Karma)
- **Report**: `coverage/index.html`

## Continuous Integration

### GitHub Actions Example

```yaml
name: CI Pipeline
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    services:
      mongodb:
        image: mongo:6.0
        ports:
          - 27017:27017
      redis:
        image: redis:7.0
        ports:
          - 6379:6379
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      - name: Cache Node modules
        uses: actions/cache@v3
        with:
          path: ~/.npm
          key: ${{ runner.os }}-node-${{ hashFiles('**/package-lock.json') }}
      - name: Run backend tests
        run: mvn test
      - name: Run frontend tests
        run: |
          cd frontend/ehealth-angular-app
          npm ci
          npm test -- --watch=false --browsers=ChromeHeadless
      - name: Generate coverage reports
        run: |
          mvn jacoco:report
          cd frontend/ehealth-angular-app
          npm run test:coverage
```

## Performance Testing

### Load Testing with JMeter

1. **Create test plan**
   - Thread Group: 100 users, ramp-up 10s
   - HTTP Requests: Login, Get Patients, Create Appointment
   - Assertions: Response time < 500ms, success status

2. **Run tests**
   ```bash
   jmeter -n -t ehealthcare-test-plan.jmx -l results.jtl
   ```

3. **Analyze results**
   - Response times
   - Error rates
   - Throughput

### Stress Testing

- Gradually increase load until system breaks
- Identify bottlenecks
- Monitor resource usage (CPU, memory, database connections)

## Security Testing

### Authentication Testing

- Test invalid tokens
- Test expired tokens
- Test missing authorization headers
- Test role-based access control

### Authorization Testing

- Test access to restricted resources
- Test admin-only endpoints
- Test cross-tenant data access

### Input Validation Testing

- SQL injection attempts
- XSS payloads
- Large payload attacks
- Malformed JSON

## Test Reporting

### Allure Reports

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.22.0</version>
    <scope>test</scope>
</dependency>
```

### Report Generation

```bash
# Generate Allure report
mvn allure:report

# Serve report
mvn allure:serve
```

## Best Practices

1. **Test First**: Write tests before implementing features
2. **Independent Tests**: Each test should be independent
3. **Descriptive Names**: Use clear, descriptive test names
4. **Arrange-Act-Assert**: Follow AAA pattern
5. **Mock External Dependencies**: Use mocks for external services
6. **Test Edge Cases**: Don't just test happy paths
7. **Continuous Testing**: Run tests on every commit
8. **Performance Benchmarks**: Set performance expectations

## Troubleshooting Tests

### Common Issues

1. **Flaky Tests**: Identify and fix non-deterministic tests
2. **Slow Tests**: Optimize test execution time
3. **Test Dependencies**: Ensure proper test isolation
4. **Environment Issues**: Use consistent test environments

### Debugging Tests

```java
// Enable debug logging
@BeforeEach
void setUp() {
    // Enable debug logging for specific packages
}

// Print test data
@Test
void debugTest() {
    System.out.println("Test data: " + testData);
    // Add breakpoints here
}
```

This comprehensive testing guide ensures the E-Health Care Platform maintains high quality and reliability through thorough testing at all levels.
