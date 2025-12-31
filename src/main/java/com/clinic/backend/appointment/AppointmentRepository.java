package com.clinic.backend.appointment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByPatient_IdOrderByDateDescTimeDesc(Long patientId, Pageable pageable);

    List<Appointment> findByDate(LocalDate date);

    List<Appointment> findByDateAndDoctorId(LocalDate date, String doctorId);
    boolean existsByPatient_Id(Long patientId);

}
