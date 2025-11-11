package com.ehealth.doctorservice.controller;

import com.ehealth.doctorservice.dto.DoctorDTO;
import com.ehealth.doctorservice.service.DoctorService;
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
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Doctor Management", description = "APIs for managing doctor profiles and availability")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new doctor", description = "Creates a new doctor profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Doctor ID, email, or license number already exists")
    })
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        log.info("Received request to create doctor: {} {}", doctorDTO.getFirstName(), doctorDTO.getLastName());
        DoctorDTO createdDoctor = doctorService.createDoctor(doctorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get doctor by ID", description = "Retrieves a doctor by their database ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<DoctorDTO> getDoctorById(
            @Parameter(description = "Doctor database ID") @PathVariable String id) {
        log.debug("Received request to get doctor by ID: {}", id);
        Optional<DoctorDTO> doctor = doctorService.getDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get doctor by doctor ID", description = "Retrieves a doctor by their doctor ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<DoctorDTO> getDoctorByDoctorId(
            @Parameter(description = "Doctor ID") @PathVariable String doctorId) {
        log.debug("Received request to get doctor by doctorId: {}", doctorId);
        Optional<DoctorDTO> doctor = doctorService.getDoctorByDoctorId(doctorId);
        return doctor.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get all doctors", description = "Retrieves all active doctors with pagination")
    @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully")
    public ResponseEntity<Page<DoctorDTO>> getAllDoctors(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.debug("Received request to get all doctors, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDTO> doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get available doctors", description = "Retrieves all available and active doctors with pagination")
    @ApiResponse(responseCode = "200", description = "Available doctors retrieved successfully")
    public ResponseEntity<Page<DoctorDTO>> getAvailableDoctors(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.debug("Received request to get available doctors, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDTO> doctors = doctorService.getAvailableDoctors(pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get doctors by specialization", description = "Retrieves doctors by their specialization")
    @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(
            @Parameter(description = "Medical specialization") @PathVariable String specialization) {
        log.debug("Received request to get doctors by specialization: {}", specialization);
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Search doctors", description = "Searches doctors by name, email, doctor ID, or specialization")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<Page<DoctorDTO>> searchDoctors(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.debug("Received search request with term: {}, page: {}, size: {}", searchTerm, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDTO> doctors = doctorService.searchDoctors(searchTerm, pageable);
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Update doctor", description = "Updates an existing doctor profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Doctor not found"),
        @ApiResponse(responseCode = "409", description = "Email or license number already exists")
    })
    public ResponseEntity<DoctorDTO> updateDoctor(
            @Parameter(description = "Doctor database ID") @PathVariable String id,
            @Valid @RequestBody DoctorDTO doctorDTO) {
        log.info("Received request to update doctor with ID: {}", id);
        DoctorDTO updatedDoctor = doctorService.updateDoctor(id, doctorDTO);
        return ResponseEntity.ok(updatedDoctor);
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Update doctor availability", description = "Updates a doctor's availability status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability updated successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<Void> updateAvailability(
            @Parameter(description = "Doctor database ID") @PathVariable String id,
            @Parameter(description = "Availability status") @RequestParam boolean available) {
        log.info("Received request to update availability for doctor ID: {} to {}", id, available);
        doctorService.updateAvailability(id, available);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete doctor", description = "Soft deletes a doctor profile (marks as inactive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<Void> deleteDoctor(
            @Parameter(description = "Doctor database ID") @PathVariable String id) {
        log.info("Received request to delete doctor with ID: {}", id);
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get doctor count", description = "Returns the total count of active doctors")
    @ApiResponse(responseCode = "200", description = "Doctor count retrieved successfully")
    public ResponseEntity<Long> getDoctorCount() {
        log.debug("Received request to get doctor count");
        long count = doctorService.getTotalDoctorCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('USER')")
    @Operation(summary = "Get available doctor count", description = "Returns the count of available and active doctors")
    @ApiResponse(responseCode = "200", description = "Available doctor count retrieved successfully")
    public ResponseEntity<Long> getAvailableDoctorCount() {
        log.debug("Received request to get available doctor count");
        long count = doctorService.getAvailableDoctorCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Operation(summary = "Get all active doctors", description = "Retrieves all active doctors without pagination")
    @ApiResponse(responseCode = "200", description = "All active doctors retrieved successfully")
    public ResponseEntity<List<DoctorDTO>> getAllActiveDoctors() {
        log.debug("Received request to get all active doctors");
        List<DoctorDTO> doctors = doctorService.getAllActiveDoctors();
        return ResponseEntity.ok(doctors);
    }
}
