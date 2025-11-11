package com.ehealth.doctorservice.service;

import com.ehealth.doctorservice.dto.DoctorDTO;
import com.ehealth.doctorservice.model.Doctor;
import com.ehealth.doctorservice.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private DoctorDTO testDoctorDTO;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testDoctorDTO = DoctorDTO.builder()
                .doctorId("DOC-001")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .gender("MALE")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .specialization("Cardiology")
                .licenseNumber("LIC123456")
                .build();

        testDoctor = Doctor.builder()
                .id("123")
                .doctorId("DOC-001")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .gender("MALE")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .specialization("Cardiology")
                .licenseNumber("LIC123456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateDoctorSuccessfully() {
        // Given
        when(doctorRepository.existsByDoctorId(anyString())).thenReturn(false);
        when(doctorRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        DoctorDTO result = doctorService.createDoctor(testDoctorDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldThrowExceptionWhenDoctorIdExists() {
        // Given
        when(doctorRepository.existsByDoctorId("DOC-001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> doctorService.createDoctor(testDoctorDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Doctor ID already exists: DOC-001");
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(doctorRepository.existsByDoctorId(anyString())).thenReturn(false);
        when(doctorRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> doctorService.createDoctor(testDoctorDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists: john.doe@example.com");
    }

    @Test
    void shouldGetDoctorById() {
        // Given
        when(doctorRepository.findById("123")).thenReturn(Optional.of(testDoctor));

        // When
        Optional<DoctorDTO> result = doctorService.getDoctorById("123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldReturnEmptyWhenDoctorNotFound() {
        // Given
        when(doctorRepository.findById("999")).thenReturn(Optional.empty());

        // When
        Optional<DoctorDTO> result = doctorService.getDoctorById("999");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateDoctorSuccessfully() {
        // Given
        DoctorDTO updateDTO = DoctorDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .specialization("Cardiology")
                .licenseNumber("LIC123456")
                .build();

        Doctor updatedDoctor = testDoctor;
        updatedDoctor.setLastName("Smith");
        updatedDoctor.setEmail("john.smith@example.com");

        when(doctorRepository.findById("123")).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        // When
        DoctorDTO result = doctorService.updateDoctor("123", updateDTO);

        // Then
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("john.smith@example.com");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentDoctor() {
        // Given
        when(doctorRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> doctorService.updateDoctor("999", testDoctorDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Doctor not found with ID: 999");
    }

    @Test
    void shouldDeleteDoctor() {
        // Given
        when(doctorRepository.findById("123")).thenReturn(Optional.of(testDoctor));

        // When
        doctorService.deleteDoctor("123");

        // Then
        verify(doctorRepository).save(any(Doctor.class));
        assertThat(testDoctor.isActive()).isFalse();
    }

    @Test
    void shouldUpdateAvailability() {
        // Given
        when(doctorRepository.findById("123")).thenReturn(Optional.of(testDoctor));

        // When
        doctorService.updateAvailability("123", false);

        // Then
        verify(doctorRepository).save(any(Doctor.class));
        assertThat(testDoctor.isAvailable()).isFalse();
    }

    @Test
    void shouldGetTotalDoctorCount() {
        // Given
        when(doctorRepository.countByActiveTrue()).thenReturn(5L);

        // When
        long count = doctorService.getTotalDoctorCount();

        // Then
        assertThat(count).isEqualTo(5L);
    }

    @Test
    void shouldGetAvailableDoctorCount() {
        // Given
        when(doctorRepository.countByAvailableTrueAndActiveTrue()).thenReturn(3L);

        // When
        long count = doctorService.getAvailableDoctorCount();

        // Then
        assertThat(count).isEqualTo(3L);
    }
}
