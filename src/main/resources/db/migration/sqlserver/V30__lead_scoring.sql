-- Add lead scoring columns to leads table
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='leads' AND COLUMN_NAME='score')
    ALTER TABLE leads ADD score INT DEFAULT 0;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='leads' AND COLUMN_NAME='score_updated_at')
    ALTER TABLE leads ADD score_updated_at DATETIME2;

-- Create lead_score_history table for tracking score changes
CREATE TABLE lead_score_history (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    company_id UNIQUEIDENTIFIER NOT NULL,
    lead_id UNIQUEIDENTIFIER NOT NULL,
    old_score INT NOT NULL,
    new_score INT NOT NULL,
    reason NVARCHAR(255),
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_lead_score_history_lead FOREIGN KEY (lead_id) REFERENCES leads(id)
);

-- Indexes for lead_score_history
CREATE INDEX idx_lead_score_history_lead ON lead_score_history(lead_id);
CREATE INDEX idx_lead_score_history_company ON lead_score_history(company_id);
CREATE INDEX idx_lead_score_history_created ON lead_score_history(created_at);

-- Index on score for sorting
CREATE INDEX idx_leads_score ON leads(score);
