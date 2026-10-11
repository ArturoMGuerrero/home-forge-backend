-- Horario de atención de la empresa: limita las horas que se ofrecen al agendar citas y las que muestra el calendario.
ALTER TABLE companies
    ADD COLUMN business_hours_start TIME NOT NULL DEFAULT '09:00',
    ADD COLUMN business_hours_end TIME NOT NULL DEFAULT '19:00',
    ADD COLUMN appointment_slot_minutes INTEGER NOT NULL DEFAULT 30,
    ADD CONSTRAINT companies_business_hours_range CHECK (business_hours_start < business_hours_end),
    ADD CONSTRAINT companies_appointment_slot_minutes CHECK (appointment_slot_minutes IN (15, 30, 60));
