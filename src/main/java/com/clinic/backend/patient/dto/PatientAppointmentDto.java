package com.clinic.backend.patient.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record PatientAppointmentDto(
        Long id,
        String doctorId,
        LocalDate date,
        LocalTime time,
        String procedure,
        String patientNameSnapshot,
        String patientPhoneSnapshot
) {}
