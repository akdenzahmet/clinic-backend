package com.clinic.backend.appointment;

import com.clinic.backend.patient.Patient;
import com.clinic.backend.patient.PatientService;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository repo;
    private final PatientService patientService;

    public AppointmentController(AppointmentRepository repo, PatientService patientService) {
        this.repo = repo;
        this.patientService = patientService;
    }

    public record AppointmentView(
            Long id,
            String doctorId,
            LocalDate date,
            java.time.LocalTime time,
            String patientName,
            String patientPhone,
            String procedure,
            Long patientId
    ) {}

    private AppointmentView toView(Appointment a) {
        Long pid = (a.getPatient() != null) ? a.getPatient().getId() : null;
        return new AppointmentView(
                a.getId(),
                a.getDoctorId(),
                a.getDate(),
                a.getTime(),
                a.getPatientName(),
                a.getPatientPhone(),
                a.getProcedure(),
                pid
        );
    }

    @GetMapping
    public List<AppointmentView> list(
            @RequestParam("date") String date,
            @RequestParam(value = "doctorId", required = false) String doctorId
    ) {
        LocalDate d = LocalDate.parse(date);

        List<Appointment> list;
        if (doctorId == null || doctorId.isBlank()) {
            list = repo.findByDate(d);
        } else {
            list = repo.findByDateAndDoctorId(d, doctorId);
        }

        return list.stream().map(this::toView).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentView create(@RequestBody CreateAppointmentRequest req) {

        if (req.patientPhone() == null || req.patientPhone().isBlank()) {
            throw new BadRequestException("Telefon zorunlu.");
        }

        Patient p = patientService.findOrCreate(req.patientName(), req.patientPhone());

        Appointment a = new Appointment();
        a.setDoctorId(req.doctorId());
        a.setDate(LocalDate.parse(req.date()));
        a.setTime(req.timeAsLocalTime());
        a.setPatientName(req.patientName());
        a.setProcedure(req.procedure());
        a.setPatient(p);
        a.setPatientPhone(req.patientPhone());

        try {
            return toView(repo.save(a));
        } catch (DataIntegrityViolationException e) {
            throw new AppointmentConflictException("Bu saat dolu.");
        }
    }

    @PutMapping("/{id}")
    public AppointmentView update(@PathVariable Long id, @RequestBody CreateAppointmentRequest req) {

        if (req.patientPhone() == null || req.patientPhone().isBlank()) {
            throw new BadRequestException("Telefon zorunlu.");
        }

        Appointment existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Randevu bulunamadı."));

        existing.setDoctorId(req.doctorId());
        existing.setDate(LocalDate.parse(req.date()));
        existing.setTime(req.timeAsLocalTime());
        existing.setPatientName(req.patientName());
        existing.setProcedure(req.procedure());

        Patient p = patientService.findOrCreate(req.patientName(), req.patientPhone());
        existing.setPatient(p);
        existing.setPatientPhone(req.patientPhone());

        try {
            return toView(repo.save(existing));
        } catch (DataIntegrityViolationException e) {
            throw new AppointmentConflictException("Bu saat dolu.");
        }
    }

    @GetMapping("/{id}")
    public AppointmentView getOne(@PathVariable Long id) {
        Appointment a = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Randevu bulunamadı."));
        return toView(a);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    static class AppointmentConflictException extends RuntimeException {
        public AppointmentConflictException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) { super(msg); }
    }

    public record CreateAppointmentRequest(
            String doctorId,
            String date,
            String time,
            String patientName,
            @JsonAlias({"patient_phone", "phone"})
            String patientPhone,
            String procedure
    ) {
        java.time.LocalTime timeAsLocalTime() {
            return java.time.LocalTime.parse(time);
        }
    }
}
