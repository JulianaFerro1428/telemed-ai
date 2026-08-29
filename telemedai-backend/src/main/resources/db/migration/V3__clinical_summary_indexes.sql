-- ============================================================
-- ÍNDICES ADICIONALES PARA RENDIMIENTO
-- ============================================================

-- Índice para post_summaries (consultas por fecha)
CREATE INDEX IF NOT EXISTS idx_post_summaries_generated_at ON post_summaries(generated_at);

-- Restricción única para garantizar una agenda por profesional
CREATE UNIQUE INDEX IF NOT EXISTS uq_agenda_professional ON agendas(professional_id);