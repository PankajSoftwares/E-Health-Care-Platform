package com.ehealth.doctorservice.repository;

import com.ehealth.doctorservice.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {

    Optional<Doctor> findByDoctorId(String doctorId);

    boolean existsByDoctorId(String doctorId);

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    Page<Doctor> findByActiveTrue(Pageable pageable);

    List<Doctor> findByActiveTrue();

    Page<Doctor> findByAvailableTrueAndActiveTrue(Pageable pageable);

    List<Doctor> findBySpecializationAndActiveTrue(String specialization);

    @Query("{ $or: [ " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'doctorId': { $regex: ?0, $options: 'i' } }, " +
           "{ 'specialization': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Doctor> findBySearchTerm(String searchTerm, Pageable pageable);

    long countByActiveTrue();

    long countByAvailableTrueAndActiveTrue();
}
