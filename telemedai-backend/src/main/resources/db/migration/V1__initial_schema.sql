-- ============================================================
-- ESQUEMA INICIAL DE TELEMED
-- Versión 1
-- ============================================================

-- ============================================================
-- TABLAS DE CATÁLOGO (entidades de referencia)
-- ============================================================

-- Roles de usuario: PACIENTE, PROFESIONAL, ADMIN
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

-- Especialidades médicas (ej. Cardiología, Dermatología)
CREATE TABLE specialties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

-- ============================================================
-- TABLAS DE IDENTIDAD Y AUTENTICACIÓN
-- ============================================================

-- Usuarios del sistema (pacientes, profesionales, administradores)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    identity_document VARCHAR(50) NOT NULL UNIQUE,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    password_hash VARCHAR(255) NOT NULL,          -- Hash bcrypt
    verified BOOLEAN NOT NULL DEFAULT FALSE,      -- Email verificado
    active BOOLEAN NOT NULL DEFAULT TRUE,         -- Cuenta activa
    registration_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_access TIMESTAMPTZ
);

-- Perfil de paciente (extiende User)
CREATE TABLE patients (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    birth_date DATE,
    phone VARCHAR(30),
    medical_history TEXT,
    description TEXT   -- Columna agregada en versión posterior
);

-- Perfil de profesional (extiende User)
CREATE TABLE professionals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    license_number VARCHAR(80) NOT NULL UNIQUE,
    specialty_id BIGINT NOT NULL REFERENCES specialties(id),
    years_experience INTEGER NOT NULL DEFAULT 0 CHECK (years_experience >= 0)
);

-- ============================================================
-- TABLAS DE AGENDA Y CITAS
-- ============================================================

-- Agenda del profesional (relación 1-1)
CREATE TABLE agendas (
    id BIGSERIAL PRIMARY KEY,
    professional_id BIGINT NOT NULL REFERENCES professionals(id) ON DELETE CASCADE
);

-- Bloques de disponibilidad (se puede extender en el futuro)
CREATE TABLE availability_blocks (
    id BIGSERIAL PRIMARY KEY,
    agenda_id BIGINT NOT NULL REFERENCES agendas(id) ON DELETE CASCADE,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (end_time > start_time)
);

-- Citas médicas (agregado principal)
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    professional_id BIGINT NOT NULL REFERENCES professionals(id),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    status VARCHAR(30) NOT NULL,                 -- CONFIRMADA, REPROGRAMADA, etc.
    preconsultation_summary_id BIGINT,
    post_summary_id BIGINT,
    cancellation_reason VARCHAR(500),
    CHECK (end_time > start_time)
);

-- Índices para consultas rápidas por profesional y paciente
CREATE INDEX idx_appointments_professional_start ON appointments(professional_id, start_time);
CREATE INDEX idx_appointments_patient_start ON appointments(patient_id, start_time);

-- ============================================================
-- TABLAS DEL AGENTE INTELIGENTE (PRECONSULTA)
-- ============================================================

-- Conversaciones del agente con pacientes
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    start_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL                  -- ACTIVA, FINALIZADA
);

-- Mensajes individuales dentro de una conversación
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender VARCHAR(20) NOT NULL,                 -- PACIENTE, AGENTE
    content TEXT NOT NULL,
    sent_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLAS DE RESÚMENES CLÍNICOS
-- ============================================================

-- Resumen de preconsulta (generado por el agente)
CREATE TABLE preconsultation_summaries (
    id BIGSERIAL PRIMARY KEY,
    consultation_reason TEXT,
    evolution_time VARCHAR(200),
    detailed_symptoms TEXT,
    relevant_history TEXT
);

-- Resumen de atención (generado por el profesional)
CREATE TABLE attention_summaries (
    id BIGSERIAL PRIMARY KEY,
    diagnosis TEXT,
    recommendations TEXT,
    medications TEXT,
    observations TEXT,
    referral TEXT
);

-- Resumen posterior (combina preconsulta + atención)
CREATE TABLE post_summaries (
    id BIGSERIAL PRIMARY KEY,
    preconsultation_summary_id BIGINT REFERENCES preconsultation_summaries(id),
    attention_summary_id BIGINT REFERENCES attention_summaries(id),
    generated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Asociar resúmenes a la cita (claves foráneas)
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointment_pre FOREIGN KEY (preconsultation_summary_id) REFERENCES preconsultation_summaries(id);
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointment_post FOREIGN KEY (post_summary_id) REFERENCES post_summaries(id);

-- ============================================================
-- TABLAS DE NOTIFICACIONES
-- ============================================================

-- Notificaciones para usuarios
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(40) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- DATOS INICIALES (CATÁLOGOS)
-- ============================================================

-- Roles predefinidos
INSERT INTO roles(name) VALUES ('PACIENTE'), ('PROFESIONAL'), ('ADMIN')
ON CONFLICT DO NOTHING;

-- Especialidades predefinidas
INSERT INTO specialties(name, description) VALUES
('Medicina General', 'Atención médica general y orientación inicial.'),
('Pediatría', 'Atención integral de población pediátrica.'),
('Dermatología', 'Atención de enfermedades de piel, cabello y uñas.'),
('Cardiología', 'Atención de enfermedades cardiovasculares.')
ON CONFLICT DO NOTHING;

-- ============================================================
-- DATOS DE PRUEBA (ADMIN)
-- ============================================================

-- Inserta un usuario administrador solo si no existe
-- Contraseña: password123 (hash bcrypt)
INSERT INTO users (full_name, email, identity_document, role_id, password_hash, verified, active, registration_date)
SELECT 'Admin', 'admin@email.com', '000000000', 3, '$2a$10$jatWIgHpGtruMJwuePTFS.7FUo4tBbbR9V3f8zeh6Zbra4qrmbxiK', true, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@email.com');

-- ============================================================
-- TABLAS DE TOKENS (SESIONES Y RECUPERACIÓN)
-- ============================================================

-- Refresh tokens persistentes para renovar sesiones
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(120) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

-- Índices para consultas rápidas
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expiration ON refresh_tokens(expires_at);

-- Tokens de recuperación de contraseña (un solo uso)
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(120) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_password_reset_tokens_user ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_expiration ON password_reset_tokens(expires_at);

-- ============================================================
-- ÍNDICES ADICIONALES PARA RENDIMIENTO
-- ============================================================

-- Índice para post_summaries (consultas por fecha)
CREATE INDEX IF NOT EXISTS idx_post_summaries_generated_at ON post_summaries(generated_at);

-- Restricción única para garantizar una agenda por profesional
CREATE UNIQUE INDEX IF NOT EXISTS uq_agenda_professional ON agendas(professional_id);