-- User activity log
CREATE TABLE user_activity (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  company_id UUID NOT NULL REFERENCES companies(id),
  user_id UUID NOT NULL REFERENCES users(id),
  activity_type VARCHAR(60) NOT NULL,
  activity_category VARCHAR(40) NOT NULL,
  entity_type VARCHAR(40),
  entity_id UUID,
  description_en VARCHAR(255),
  description_es VARCHAR(255),
  ip_address VARCHAR(45),
  user_agent VARCHAR(255),
  metadata TEXT,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_activity_user_id ON user_activity(user_id);
CREATE INDEX idx_user_activity_company_id ON user_activity(company_id);
CREATE INDEX idx_user_activity_created_at ON user_activity(created_at DESC);
CREATE INDEX idx_user_activity_type ON user_activity(activity_type);
