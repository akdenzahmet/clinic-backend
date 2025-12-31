package com.clinic.backend.patient.dto;

public record PatientSummaryDto(
        Long id,
        String fullName,
        String phone
) {}
