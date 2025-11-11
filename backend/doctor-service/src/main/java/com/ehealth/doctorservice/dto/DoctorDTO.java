package com.ehealth.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {

    private String id;

    @NotBlank(message = "Doctor ID is required")
    @Size(min = 3, max = 20, message = "Doctor ID must be between 3 and 20 characters")
    private String doctorId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phone;

    private String address;

    private String city;

    private String state;

    @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$", message = "Zip code should be valid")
    private String zipCode;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String department;

    private List<String> qualifications;

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years cannot exceed 50")
    private Integer experienceYears;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    private List<String> languages;

    private boolean available = true;

    private boolean active = true;
}
