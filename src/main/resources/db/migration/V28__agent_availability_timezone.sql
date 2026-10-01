-- La entidad AgentAvailability usa una zona horaria que V25 no creó.
ALTER TABLE agent_availability
  ADD COLUMN IF NOT EXISTS timezone VARCHAR(64) NOT NULL DEFAULT 'America/Mexico_City';
