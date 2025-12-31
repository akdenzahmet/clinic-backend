package com.clinic.backend.patient;

import com.clinic.backend.appointment.AppointmentRepository;
import com.clinic.backend.patient.dto.PatientAppointmentDto;
import com.clinic.backend.patient.dto.PatientDetailDto;
import com.clinic.backend.patient.dto.PatientSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientToothTreatmentRepository toothRepo;
    private final PatientQueryService queryService;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public PatientController(
            PatientToothTreatmentRepository toothRepo,
            PatientQueryService queryService,
            PatientRepository patientRepo,
            AppointmentRepository appointmentRepo
    ) {
        this.toothRepo = toothRepo;
        this.queryService = queryService;
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
    }

    // SEARCH / DETAIL / APPTS
    @GetMapping
    public Page<PatientSummaryDto> search(@RequestParam(required = false) String q, Pageable pageable) {
        return queryService.search(q, pageable);
    }

    @GetMapping("/{id}")
    public PatientDetailDto detail(@PathVariable Long id) {
        return queryService.getDetail(id);
    }

    @GetMapping("/{id}/appointments")
    public Page<PatientAppointmentDto> appointments(@PathVariable Long id, Pageable pageable) {
        return queryService.getAppointments(id, pageable);
    }

    // TOOTH TREATMENTS API
    // Listele
    @GetMapping("/{id}/tooth-treatments")
    public List<ToothTreatmentDto> getToothTreatments(@PathVariable Long id) {
        if (!patientRepo.existsById(id)) throw new NotFoundException("Hasta bulunamadı.");

        return toothRepo.findByPatient_IdOrderByCreatedAtDesc(id)
                .stream()
                .map(ToothTreatmentDto::from)
                .toList();
    }

    // sadece lazım olan alanları dön
    public record ToothTreatmentDto(
            Long id,
            String toothCode,
            String procedure,
            boolean done,
            java.time.OffsetDateTime createdAt,
            java.time.OffsetDateTime updatedAt
    ) {
        static ToothTreatmentDto from(PatientToothTreatment t) {
            return new ToothTreatmentDto(
                    t.getId(),
                    t.getToothCode(),
                    t.getProcedure(),
                    t.isDone(),
                    t.getCreatedAt(),
                    t.getUpdatedAt()
            );
        }
    }


    // Ekle
    public record CreateToothTreatmentRequest(String toothCode, String procedure) {}

    @PostMapping("/{id}/tooth-treatments")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientToothTreatment addToothTreatment(
            @PathVariable Long id,
            @RequestBody CreateToothTreatmentRequest req
    ) {
        if (req == null || req.toothCode() == null || req.toothCode().trim().isEmpty()) {
            throw new BadRequestException("Diş kodu zorunlu.");
        }
        if (req.procedure() == null || req.procedure().trim().isEmpty()) {
            throw new BadRequestException("Müdahale zorunlu.");
        }

        Patient p = patientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı."));

        PatientToothTreatment t = new PatientToothTreatment();
        t.setPatient(p);
        t.setToothCode(req.toothCode().trim());
        t.setProcedure(req.procedure().trim());
        t.setDone(false);

        return toothRepo.save(t);
    }

    // Done toggle (true/false)
    public record SetDoneRequest(boolean done) {}

    @PutMapping("/{id}/tooth-treatments/{tid}/done")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setDone(
            @PathVariable Long id,
            @PathVariable Long tid,
            @RequestBody SetDoneRequest req
    ) {
        if (req == null) throw new BadRequestException("done bilgisi zorunlu.");

        PatientToothTreatment t = toothRepo.findByIdAndPatient_Id(tid, id)
                .orElseThrow(() -> new NotFoundException("Kayıt bulunamadı."));

        t.setDone(req.done());
        toothRepo.save(t);
    }


    // Body’siz direkt true yapan kısa endpoint
    // PUT /patients/{id}/tooth-treatments/{tid}/done/true
    @PutMapping("/{id}/tooth-treatments/{tid}/done/true")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markDoneTrue(@PathVariable Long id, @PathVariable Long tid) {
        PatientToothTreatment t = toothRepo.findByIdAndPatient_Id(tid, id)
                .orElseThrow(() -> new NotFoundException("Kayıt bulunamadı."));
        t.setDone(true);
        toothRepo.save(t);
    }

    // Sil
    @DeleteMapping("/{id}/tooth-treatments/{tid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToothTreatment(@PathVariable Long id, @PathVariable Long tid) {
        PatientToothTreatment t = toothRepo.findByIdAndPatient_Id(tid, id)
                .orElseThrow(() -> new NotFoundException("Kayıt bulunamadı."));
        toothRepo.delete(t);
    }

    // UPDATE (fullName + phone + notes)
    public record UpdatePatientRequest(String fullName, String phone, String notes) {}

    @PutMapping("/{id}")
    public PatientDetailDto update(@PathVariable Long id, @RequestBody UpdatePatientRequest req) {
        Patient p = patientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı."));

        if (req.fullName() == null || req.fullName().trim().isEmpty()) {
            throw new BadRequestException("Ad Soyad zorunlu.");
        }
        if (req.phone() == null || req.phone().trim().isEmpty()) {
            throw new BadRequestException("Telefon zorunlu.");
        }

        p.setFullName(req.fullName().trim());
        p.setPhone(req.phone().trim());

        if (req.notes() != null) {
            p.setNotes(req.notes());
        }

        patientRepo.save(p);
        return queryService.getDetail(id);
    }

    // DELETE PATIENT
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!patientRepo.existsById(id)) {
            throw new NotFoundException("Hasta bulunamadı.");
        }

        if (appointmentRepo.existsByPatient_Id(id)) {
            throw new ConflictException("Bu hastanın randevusu var, silinemez.");
        }

        patientRepo.deleteById(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    static class ConflictException extends RuntimeException {
        public ConflictException(String msg) { super(msg); }
    }
}
