CREATE TABLE IF NOT EXISTS patients (
                                        id BIGSERIAL PRIMARY KEY,
                                        full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_patients_phone ON patients(phone);
