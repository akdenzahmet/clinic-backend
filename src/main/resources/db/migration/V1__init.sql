create extension if not exists "uuid-ossp";

create table if not exists users (
                                     id uuid primary key default uuid_generate_v4(),
    username varchar(64) not null unique,
    password_hash varchar(255) not null,
    role varchar(32) not null,
    active boolean not null default true,
    created_at timestamp not null default now()
    );
