package com.ehealth.patientservice.repository;

import com.ehealth.patientservice.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByPatientId(String patientId);

    boolean existsByPatientId(String patientId);

    boolean existsByEmail(String email);

    Page<Patient> findByActiveTrue(Pageable pageable);

    List<Patient> findByActiveTrue();

    @Query("{ $or: [ " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'patientId': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Patient> findBySearchTerm(String searchTerm, Pageable pageable);

    long countByActiveTrue();
}
