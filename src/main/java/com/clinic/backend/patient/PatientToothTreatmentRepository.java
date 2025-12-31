package com.clinic.backend.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientToothTreatmentRepository extends JpaRepository<PatientToothTreatment, Long> {

    List<PatientToothTreatment> findByPatient_IdOrderByCreatedAtDesc(Long patientId);

    Optional<PatientToothTreatment> findByIdAndPatient_Id(Long id, Long patientId);
}
