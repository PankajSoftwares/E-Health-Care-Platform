package com.ehealth.patientservice.controller;

import com.ehealth.patientservice.dto.PatientDTO;
import com.ehealth.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient Management", description = "APIs for managing patient records")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Create a new patient", description = "Creates a new patient record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Patient ID or email already exists")
    })
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        log.info("Received request to create patient: {}", patientDTO.getFirstName() + " " + patientDTO.getLastName());
        PatientDTO createdPatient = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get patient by ID", description = "Retrieves a patient by their database ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientDTO> getPatientById(
            @Parameter(description = "Patient database ID") @PathVariable String id) {
        log.debug("Received request to get patient by ID: {}", id);
        Optional<PatientDTO> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get patient by patient ID", description = "Retrieves a patient by their patient ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientDTO> getPatientByPatientId(
            @Parameter(description = "Patient ID") @PathVariable String patientId) {
        log.debug("Received request to get patient by patientId: {}", patientId);
        Optional<PatientDTO> patient = patientService.getPatientByPatientId(patientId);
        return patient.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get all patients", description = "Retrieves all active patients with pagination")
    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    public ResponseEntity<Page<PatientDTO>> getAllPatients(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.debug("Received request to get all patients, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDTO> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Search patients", description = "Searches patients by name, email, or patient ID")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<Page<PatientDTO>> searchPatients(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.debug("Received search request with term: {}, page: {}, size: {}", searchTerm, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDTO> patients = patientService.searchPatients(searchTerm, pageable);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Update patient", description = "Updates an existing patient record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<PatientDTO> updatePatient(
            @Parameter(description = "Patient database ID") @PathVariable String id,
            @Valid @RequestBody PatientDTO patientDTO) {
        log.info("Received request to update patient with ID: {}", id);
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete patient", description = "Soft deletes a patient record (marks as inactive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient database ID") @PathVariable String id) {
        log.info("Received request to delete patient with ID: {}", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get patient count", description = "Returns the total count of active patients")
    @ApiResponse(responseCode = "200", description = "Patient count retrieved successfully")
    public ResponseEntity<Long> getPatientCount() {
        log.debug("Received request to get patient count");
        long count = patientService.getTotalPatientCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get all active patients", description = "Retrieves all active patients without pagination")
    @ApiResponse(responseCode = "200", description = "All active patients retrieved successfully")
    public ResponseEntity<List<PatientDTO>> getAllActivePatients() {
        log.debug("Received request to get all active patients");
        List<PatientDTO> patients = patientService.getAllActivePatients();
        return ResponseEntity.ok(patients);
    }
}
