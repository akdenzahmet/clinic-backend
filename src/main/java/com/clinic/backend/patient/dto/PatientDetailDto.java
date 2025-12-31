package com.clinic.backend.patient.dto;

import java.time.LocalDateTime;

public record PatientDetailDto(
        Long id,
        String fullName,
        String phone,
        LocalDateTime createdAt,
        String notes
) {}
