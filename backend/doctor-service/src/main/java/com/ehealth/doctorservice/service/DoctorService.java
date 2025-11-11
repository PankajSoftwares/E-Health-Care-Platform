package com.ehealth.doctorservice.service;

import com.ehealth.doctorservice.dto.DoctorDTO;
import com.ehealth.doctorservice.model.Doctor;
import com.ehealth.doctorservice.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        log.info("Creating new doctor with doctorId: {}", doctorDTO.getDoctorId());

        if (doctorRepository.existsByDoctorId(doctorDTO.getDoctorId())) {
            throw new IllegalArgumentException("Doctor ID already exists: " + doctorDTO.getDoctorId());
        }

        if (doctorRepository.existsByEmail(doctorDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + doctorDTO.getEmail());
        }

        if (doctorRepository.existsByLicenseNumber(doctorDTO.getLicenseNumber())) {
            throw new IllegalArgumentException("License number already exists: " + doctorDTO.getLicenseNumber());
        }

        Doctor doctor = mapToEntity(doctorDTO);
        doctor.setDoctorId(generateDoctorId());
        doctor.setCreatedAt(LocalDateTime.now());
        doctor.setUpdatedAt(LocalDateTime.now());

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor created successfully with ID: {}", savedDoctor.getId());

        return mapToDTO(savedDoctor);
    }

    public Optional<DoctorDTO> getDoctorById(String id) {
        log.debug("Fetching doctor by ID: {}", id);
        return doctorRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<DoctorDTO> getDoctorByDoctorId(String doctorId) {
        log.debug("Fetching doctor by doctorId: {}", doctorId);
        return doctorRepository.findByDoctorId(doctorId).map(this::mapToDTO);
    }

    public Page<DoctorDTO> getAllDoctors(Pageable pageable) {
        log.debug("Fetching all doctors with pagination");
        return doctorRepository.findByActiveTrue(pageable).map(this::mapToDTO);
    }

    public Page<DoctorDTO> getAvailableDoctors(Pageable pageable) {
        log.debug("Fetching available doctors with pagination");
        return doctorRepository.findByAvailableTrueAndActiveTrue(pageable).map(this::mapToDTO);
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        log.debug("Fetching doctors by specialization: {}", specialization);
        return doctorRepository.findBySpecializationAndActiveTrue(specialization)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<DoctorDTO> searchDoctors(String searchTerm, Pageable pageable) {
        log.debug("Searching doctors with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllDoctors(pageable);
        }
        return doctorRepository.findBySearchTerm(searchTerm.trim(), pageable).map(this::mapToDTO);
    }

    public DoctorDTO updateDoctor(String id, DoctorDTO doctorDTO) {
        log.info("Updating doctor with ID: {}", id);

        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));

        // Check for duplicate doctorId if it's being changed
        if (!existingDoctor.getDoctorId().equals(doctorDTO.getDoctorId()) &&
            doctorRepository.existsByDoctorId(doctorDTO.getDoctorId())) {
            throw new IllegalArgumentException("Doctor ID already exists: " + doctorDTO.getDoctorId());
        }

        // Check for duplicate email if it's being changed
        if (!existingDoctor.getEmail().equals(doctorDTO.getEmail()) &&
            doctorRepository.existsByEmail(doctorDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + doctorDTO.getEmail());
        }

        // Check for duplicate license number if it's being changed
        if (!existingDoctor.getLicenseNumber().equals(doctorDTO.getLicenseNumber()) &&
            doctorRepository.existsByLicenseNumber(doctorDTO.getLicenseNumber())) {
            throw new IllegalArgumentException("License number already exists: " + doctorDTO.getLicenseNumber());
        }

        Doctor updatedDoctor = mapToEntity(doctorDTO);
        updatedDoctor.setId(id);
        updatedDoctor.setDoctorId(existingDoctor.getDoctorId()); // Keep original doctorId
        updatedDoctor.setCreatedAt(existingDoctor.getCreatedAt());
        updatedDoctor.setUpdatedAt(LocalDateTime.now());

        Doctor savedDoctor = doctorRepository.save(updatedDoctor);
        log.info("Doctor updated successfully with ID: {}", savedDoctor.getId());

        return mapToDTO(savedDoctor);
    }

    public void deleteDoctor(String id) {
        log.info("Deleting doctor with ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));

        // Soft delete - mark as inactive
        doctor.setActive(false);
        doctor.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctor);

        log.info("Doctor marked as inactive with ID: {}", id);
    }

    public void updateAvailability(String id, boolean available) {
        log.info("Updating availability for doctor ID: {} to {}", id, available);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));

        doctor.setAvailable(available);
        doctor.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctor);

        log.info("Doctor availability updated successfully");
    }

    public long getTotalDoctorCount() {
        return doctorRepository.countByActiveTrue();
    }

    public long getAvailableDoctorCount() {
        return doctorRepository.countByAvailableTrueAndActiveTrue();
    }

    public List<DoctorDTO> getAllActiveDoctors() {
        log.debug("Fetching all active doctors");
        return doctorRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private String generateDoctorId() {
        return "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private DoctorDTO mapToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setDoctorId(doctor.getDoctorId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setDateOfBirth(doctor.getDateOfBirth());
        dto.setGender(doctor.getGender());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setAddress(doctor.getAddress());
        dto.setCity(doctor.getCity());
        dto.setState(doctor.getState());
        dto.setZipCode(doctor.getZipCode());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDepartment(doctor.getDepartment());
        dto.setQualifications(doctor.getQualifications());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setBio(doctor.getBio());
        dto.setLanguages(doctor.getLanguages());
        dto.setAvailable(doctor.isAvailable());
        dto.setActive(doctor.isActive());
        return dto;
    }

    private Doctor mapToEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());
        doctor.setDoctorId(dto.getDoctorId());
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setDateOfBirth(dto.getDateOfBirth());
        doctor.setGender(dto.getGender());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setAddress(dto.getAddress());
        doctor.setCity(dto.getCity());
        doctor.setState(dto.getState());
        doctor.setZipCode(dto.getZipCode());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setDepartment(dto.getDepartment());
        doctor.setQualifications(dto.getQualifications());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setBio(dto.getBio());
        doctor.setLanguages(dto.getLanguages());
        doctor.setAvailable(dto.isAvailable());
        doctor.setActive(dto.isActive());
        return doctor;
    }
}
