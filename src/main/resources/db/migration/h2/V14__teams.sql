-- Teams table
CREATE TABLE teams (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  company_id UUID NOT NULL REFERENCES companies(id),
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  leader_id UUID REFERENCES users(id),
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP WITH TIME ZONE
);

-- Team members (many-to-many User <-> Team)
CREATE TABLE team_members (
  team_id UUID NOT NULL REFERENCES teams(id),
  user_id UUID NOT NULL REFERENCES users(id),
  joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(team_id, user_id)
);

-- Add team assignment to leads
ALTER TABLE leads ADD COLUMN assigned_team_id UUID REFERENCES teams(id);
ALTER TABLE leads ADD COLUMN assigned_user_id UUID REFERENCES users(id);

-- Add team assignment to properties
ALTER TABLE properties ADD COLUMN assigned_team_id UUID REFERENCES teams(id);
ALTER TABLE properties ADD COLUMN assigned_user_id UUID REFERENCES users(id);
