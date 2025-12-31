ALTER TABLE public.appointments
    ADD COLUMN IF NOT EXISTS patient_id BIGINT;

ALTER TABLE public.appointments
    ADD COLUMN IF NOT EXISTS patient_phone VARCHAR(30);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'fk_appointments_patient'
  ) THEN
ALTER TABLE public.appointments
    ADD CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
            REFERENCES public.patients(id)
            ON DELETE SET NULL;
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_appointments_patient_id
    ON public.appointments(patient_id);
