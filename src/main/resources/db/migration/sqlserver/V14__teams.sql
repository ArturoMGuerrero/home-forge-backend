-- Teams table
CREATE TABLE teams (
  id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  leader_id UNIQUEIDENTIFIER REFERENCES users(id),
  is_active BIT NOT NULL DEFAULT 1,
  created_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
  updated_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
  deleted_at DATETIMEOFFSET
);

-- Team members (many-to-many User <-> Team)
CREATE TABLE team_members (
  team_id UNIQUEIDENTIFIER NOT NULL REFERENCES teams(id),
  user_id UNIQUEIDENTIFIER NOT NULL REFERENCES users(id),
  joined_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
  PRIMARY KEY(team_id, user_id)
);

-- Add team assignment to leads
ALTER TABLE leads ADD assigned_team_id UNIQUEIDENTIFIER REFERENCES teams(id);
ALTER TABLE leads ADD assigned_user_id UNIQUEIDENTIFIER REFERENCES users(id);

-- Add team assignment to properties
ALTER TABLE properties ADD assigned_team_id UNIQUEIDENTIFIER REFERENCES teams(id);
ALTER TABLE properties ADD assigned_user_id UNIQUEIDENTIFIER REFERENCES users(id);
