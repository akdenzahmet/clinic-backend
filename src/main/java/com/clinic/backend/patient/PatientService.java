package com.clinic.backend.patient;

import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public Patient findOrCreate(String fullName, String phone) {
        String normalizedPhone = normalizePhone(phone);

        return repo.findByPhone(normalizedPhone)
                .orElseGet(() -> repo.save(new Patient(fullName, normalizedPhone)));
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        // sadece rakam bırak
        return phone.replaceAll("\\D", "");
    }
}
