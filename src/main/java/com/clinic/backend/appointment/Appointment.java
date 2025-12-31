package com.clinic.backend.appointment;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import com.clinic.backend.patient.Patient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctor_date_time", columnNames = {"doctor_id", "date", "time"})
        }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name="doctor_id", nullable = false, length = 50)
    private String doctorId; // "male" / "female" (şimdilik)

    @Setter
    @Column(nullable = false)
    private LocalDate date;

    @Setter
    @Column(nullable = false)
    private LocalTime time; // 09:15

    @Setter
    @Column(name="patient_name", nullable = false, length = 150)
    private String patientName;

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Setter
    @Column(name="patient_phone", length = 30)
    private String patientPhone;

    @Setter
    @Column(length = 200)
    private String procedure; // dolgu, kanal vs

    public Appointment() {}


}
