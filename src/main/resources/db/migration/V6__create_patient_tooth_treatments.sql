CREATE TABLE IF NOT EXISTS patient_tooth_treatments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,

    tooth_code VARCHAR(3) NOT NULL,           -- 11..48
    procedure  VARCHAR(120) NOT NULL,         -- kanal/dolgu vs
    done       BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_ptt_patient ON patient_tooth_treatments(patient_id);
CREATE INDEX IF NOT EXISTS idx_ptt_patient_tooth ON patient_tooth_treatments(patient_id, tooth_code);
