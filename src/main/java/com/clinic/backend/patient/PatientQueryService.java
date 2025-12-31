package com.clinic.backend.patient;

import com.clinic.backend.appointment.Appointment;
import com.clinic.backend.appointment.AppointmentRepository;
import com.clinic.backend.patient.dto.PatientAppointmentDto;
import com.clinic.backend.patient.dto.PatientDetailDto;
import com.clinic.backend.patient.dto.PatientSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PatientQueryService {

    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public PatientQueryService(PatientRepository patientRepo, AppointmentRepository appointmentRepo) {
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public Page<PatientSummaryDto> search(String q, Pageable pageable) {
        String query = (q == null) ? "" : q.trim();
        return patientRepo
                .findByFullNameContainingIgnoreCaseOrPhoneContaining(query, query, pageable)
                .map(p -> new PatientSummaryDto(p.getId(), p.getFullName(), p.getPhone()));
    }

    public PatientDetailDto getDetail(Long id) {
        Patient p = patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return new PatientDetailDto(p.getId(), p.getFullName(), p.getPhone(), p.getCreatedAt(),p.getNotes());
    }

    public Page<PatientAppointmentDto> getAppointments(Long patientId, Pageable pageable) {
        return appointmentRepo
                .findByPatient_IdOrderByDateDescTimeDesc(patientId, pageable)
                .map(this::toDto);
    }

    private PatientAppointmentDto toDto(Appointment a) {
        return new PatientAppointmentDto(
                a.getId(),
                a.getDoctorId(),
                a.getDate(),
                a.getTime(),
                a.getProcedure(),
                a.getPatientName(),
                a.getPatientPhone()
        );
    }
}
