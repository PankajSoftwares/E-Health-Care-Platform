package com.ehealth.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private String id;

    @NotBlank(message = "Patient ID is required")
    @Size(min = 3, max = 20, message = "Patient ID must be between 3 and 20 characters")
    private String patientId;

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

    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Emergency contact phone should be valid")
    private String emergencyContactPhone;

    private String bloodType;

    private List<String> allergies;

    private List<String> medicalConditions;

    private String insuranceProvider;

    private String insurancePolicyNumber;

    private boolean active = true;
}
