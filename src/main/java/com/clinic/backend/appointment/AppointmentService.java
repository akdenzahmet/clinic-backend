package com.clinic.backend.appointment;

import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }
}
