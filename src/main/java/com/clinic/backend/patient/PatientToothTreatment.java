package com.clinic.backend.patient;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "patient_tooth_treatments",
        indexes = {
                @Index(name = "idx_ptt_patient", columnList = "patient_id"),
                @Index(name = "idx_ptt_patient_tooth", columnList = "patient_id,tooth_code")
        }
)
public class PatientToothTreatment {

    // getters/setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Setter
    @Column(name = "tooth_code", nullable = false, length = 3)
    private String toothCode; // "11".."48"

    @Setter
    @Column(name = "procedure", nullable = false, length = 120)
    private String procedure; // "dolgu" vs

    @Setter
    @Column(name = "done", nullable = false)
    private boolean done = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}
