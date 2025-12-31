package com.clinic.backend.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPhone(String phone);

    // arama: isim veya telefon
    Page<Patient> findByFullNameContainingIgnoreCaseOrPhoneContaining(
            String fullName, String phone, Pageable pageable
    );
}
