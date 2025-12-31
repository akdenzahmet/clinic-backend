package com.clinic.backend.patient;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Patient.java
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Patient() {}

    public Patient(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    // getters setters
}
