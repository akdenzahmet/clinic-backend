create table if not exists appointments (
                                            id bigserial primary key,
                                            doctor_id varchar(50) not null,
    date date not null,
    time time not null,
    patient_name varchar(150) not null,
    procedure varchar(200),
    constraint uk_doctor_date_time unique (doctor_id, date, time)
    );
