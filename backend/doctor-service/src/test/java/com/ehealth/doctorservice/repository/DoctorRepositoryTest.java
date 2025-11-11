package com.ehealth.doctorservice.repository;

import com.ehealth.doctorservice.model.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class DoctorRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();

        testDoctor = Doctor.builder()
                .doctorId("DOC-001")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .gender("MALE")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .specialization("Cardiology")
                .licenseNumber("LIC123456")
                .available(true)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveAndFindDoctorById() {
        // When
        Doctor savedDoctor = doctorRepository.save(testDoctor);

        // Then
        assertThat(savedDoctor.getId()).isNotNull();
        assertThat(savedDoctor.getDoctorId()).isEqualTo("DOC-001");

        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldFindDoctorByDoctorId() {
        // Given
        doctorRepository.save(testDoctor);

        // When
        Optional<Doctor> foundDoctor = doctorRepository.findByDoctorId("DOC-001");

        // Then
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldCheckExistenceByDoctorId() {
        // Given
        doctorRepository.save(testDoctor);

        // When & Then
        assertThat(doctorRepository.existsByDoctorId("DOC-001")).isTrue();
        assertThat(doctorRepository.existsByDoctorId("DOC-999")).isFalse();
    }

    @Test
    void shouldCheckExistenceByEmail() {
        // Given
        doctorRepository.save(testDoctor);

        // When & Then
        assertThat(doctorRepository.existsByEmail("john.doe@example.com")).isTrue();
        assertThat(doctorRepository.existsByEmail("jane.doe@example.com")).isFalse();
    }

    @Test
    void shouldCheckExistenceByLicenseNumber() {
        // Given
        doctorRepository.save(testDoctor);

        // When & Then
        assertThat(doctorRepository.existsByLicenseNumber("LIC123456")).isTrue();
        assertThat(doctorRepository.existsByLicenseNumber("LIC999999")).isFalse();
    }

    @Test
    void shouldFindActiveDoctors() {
        // Given
        doctorRepository.save(testDoctor);

        Doctor inactiveDoctor = Doctor.builder()
                .doctorId("DOC-002")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .specialization("Neurology")
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(inactiveDoctor);

        // When
        List<Doctor> activeDoctors = doctorRepository.findByActiveTrue();

        // Then
        assertThat(activeDoctors).hasSize(1);
        assertThat(activeDoctors.get(0).getDoctorId()).isEqualTo("DOC-001");
    }

    @Test
    void shouldFindAvailableActiveDoctors() {
        // Given
        doctorRepository.save(testDoctor);

        Doctor unavailableDoctor = Doctor.builder()
                .doctorId("DOC-002")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .specialization("Neurology")
                .available(false)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(unavailableDoctor);

        // When
        List<Doctor> availableDoctors = doctorRepository.findByAvailableTrueAndActiveTrue();

        // Then
        assertThat(availableDoctors).hasSize(1);
        assertThat(availableDoctors.get(0).getDoctorId()).isEqualTo("DOC-001");
    }

    @Test
    void shouldFindDoctorsBySpecialization() {
        // Given
        doctorRepository.save(testDoctor);

        Doctor anotherCardiologist = Doctor.builder()
                .doctorId("DOC-002")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .specialization("Cardiology")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(anotherCardiologist);

        Doctor neurologist = Doctor.builder()
                .doctorId("DOC-003")
                .firstName("Bob")
                .lastName("Johnson")
                .email("bob.johnson@example.com")
                .specialization("Neurology")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(neurologist);

        // When
        List<Doctor> cardiologists = doctorRepository.findBySpecializationAndActiveTrue("Cardiology");

        // Then
        assertThat(cardiologists).hasSize(2);
        assertThat(cardiologists.stream().map(Doctor::getDoctorId))
                .containsExactlyInAnyOrder("DOC-001", "DOC-002");
    }

    @Test
    void shouldSearchDoctorsByName() {
        // Given
        doctorRepository.save(testDoctor);

        Doctor anotherDoctor = Doctor.builder()
                .doctorId("DOC-002")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .specialization("Neurology")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(anotherDoctor);

        // When
        List<Doctor> searchResults = doctorRepository.findBySearchTerm("Doe", null).getContent();

        // Then
        assertThat(searchResults).hasSize(2);
        assertThat(searchResults.stream().map(Doctor::getLastName))
                .allMatch(name -> name.equals("Doe"));
    }

    @Test
    void shouldCountActiveDoctors() {
        // Given
        doctorRepository.save(testDoctor);

        Doctor inactiveDoctor = Doctor.builder()
                .doctorId("DOC-002")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .specialization("Neurology")
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(inactiveDoctor);

        // When
        long count = doctorRepository.countByActiveTrue();

        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCountAvailableActiveDoctors() {
        // Given
        doctorRepository.save(testDoctor);

        Doctor unavailableDoctor = Doctor.builder()
                .doctorId("DOC-002")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .specialization("Neurology")
                .available(false)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctorRepository.save(unavailableDoctor);

        // When
        long count = doctorRepository.countByAvailableTrueAndActiveTrue();

        // Then
        assertThat(count).isEqualTo(1);
    }
}
