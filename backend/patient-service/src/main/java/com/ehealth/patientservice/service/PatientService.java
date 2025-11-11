package com.ehealth.patientservice.service;

import com.ehealth.patientservice.dto.PatientDTO;
import com.ehealth.patientservice.model.Patient;
import com.ehealth.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientDTO createPatient(PatientDTO patientDTO) {
        log.info("Creating new patient with patientId: {}", patientDTO.getPatientId());

        if (patientRepository.existsByPatientId(patientDTO.getPatientId())) {
            throw new IllegalArgumentException("Patient ID already exists: " + patientDTO.getPatientId());
        }

        if (patientRepository.existsByEmail(patientDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + patientDTO.getEmail());
        }

        Patient patient = mapToEntity(patientDTO);
        patient.setPatientId(generatePatientId());
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created successfully with ID: {}", savedPatient.getId());

        return mapToDTO(savedPatient);
    }

    public Optional<PatientDTO> getPatientById(String id) {
        log.debug("Fetching patient by ID: {}", id);
        return patientRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<PatientDTO> getPatientByPatientId(String patientId) {
        log.debug("Fetching patient by patientId: {}", patientId);
        return patientRepository.findByPatientId(patientId).map(this::mapToDTO);
    }

    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        log.debug("Fetching all patients with pagination");
        return patientRepository.findByActiveTrue(pageable).map(this::mapToDTO);
    }

    public Page<PatientDTO> searchPatients(String searchTerm, Pageable pageable) {
        log.debug("Searching patients with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPatients(pageable);
        }
        return patientRepository.findBySearchTerm(searchTerm.trim(), pageable).map(this::mapToDTO);
    }

    public PatientDTO updatePatient(String id, PatientDTO patientDTO) {
        log.info("Updating patient with ID: {}", id);

        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));

        // Check for duplicate patientId if it's being changed
        if (!existingPatient.getPatientId().equals(patientDTO.getPatientId()) &&
            patientRepository.existsByPatientId(patientDTO.getPatientId())) {
            throw new IllegalArgumentException("Patient ID already exists: " + patientDTO.getPatientId());
        }

        // Check for duplicate email if it's being changed
        if (!existingPatient.getEmail().equals(patientDTO.getEmail()) &&
            patientRepository.existsByEmail(patientDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + patientDTO.getEmail());
        }

        Patient updatedPatient = mapToEntity(patientDTO);
        updatedPatient.setId(id);
        updatedPatient.setPatientId(existingPatient.getPatientId()); // Keep original patientId
        updatedPatient.setCreatedAt(existingPatient.getCreatedAt());
        updatedPatient.setUpdatedAt(LocalDateTime.now());

        Patient savedPatient = patientRepository.save(updatedPatient);
        log.info("Patient updated successfully with ID: {}", savedPatient.getId());

        return mapToDTO(savedPatient);
    }

    public void deletePatient(String id) {
        log.info("Deleting patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));

        // Soft delete - mark as inactive
        patient.setActive(false);
        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        log.info("Patient marked as inactive with ID: {}", id);
    }

    public long getTotalPatientCount() {
        return patientRepository.countByActiveTrue();
    }

    public List<PatientDTO> getAllActivePatients() {
        log.debug("Fetching all active patients");
        return patientRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private String generatePatientId() {
        return "PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setPatientId(patient.getPatientId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhone());
        dto.setAddress(patient.getAddress());
        dto.setCity(patient.getCity());
        dto.setState(patient.getState());
        dto.setZipCode(patient.getZipCode());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        dto.setBloodType(patient.getBloodType());
        dto.setAllergies(patient.getAllergies());
        dto.setMedicalConditions(patient.getMedicalConditions());
        dto.setInsuranceProvider(patient.getInsuranceProvider());
        dto.setInsurancePolicyNumber(patient.getInsurancePolicyNumber());
        dto.setActive(patient.isActive());
        return dto;
    }

    private Patient mapToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setPatientId(dto.getPatientId());
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setCity(dto.getCity());
        patient.setState(dto.getState());
        patient.setZipCode(dto.getZipCode());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setBloodType(dto.getBloodType());
        patient.setAllergies(dto.getAllergies());
        patient.setMedicalConditions(dto.getMedicalConditions());
        patient.setInsuranceProvider(dto.getInsuranceProvider());
        patient.setInsurancePolicyNumber(dto.getInsurancePolicyNumber());
        patient.setActive(dto.isActive());
        return patient;
    }
}
