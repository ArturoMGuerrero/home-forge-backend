-- SQL Server version
-- Note: subscription_plans has UNIQUE(code, country_code) constraint that must be dropped before altering columns

-- Drop unique constraint on subscription_plans temporarily
DECLARE @ConstraintName nvarchar(200)
SELECT @ConstraintName = Name FROM SYS.DEFAULT_CONSTRAINTS
WHERE PARENT_OBJECT_ID = OBJECT_ID('subscription_plans')
AND PARENT_COLUMN_ID = (SELECT column_id FROM sys.columns WHERE NAME = N'country_code' AND object_id = OBJECT_ID(N'subscription_plans'))
IF @ConstraintName IS NOT NULL
    EXEC('ALTER TABLE subscription_plans DROP CONSTRAINT ' + @ConstraintName)

-- Drop the UNIQUE constraint (we'll have to identify it first)
DECLARE @UniqueConstraint nvarchar(200)
SELECT @UniqueConstraint = kc.name
FROM sys.key_constraints kc
INNER JOIN sys.index_columns ic ON kc.parent_object_id = ic.object_id AND kc.unique_index_id = ic.index_id
INNER JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
WHERE kc.parent_object_id = OBJECT_ID('subscription_plans') AND c.name = 'country_code' AND kc.type = 'UQ'
IF @UniqueConstraint IS NOT NULL
    EXEC('ALTER TABLE subscription_plans DROP CONSTRAINT ' + @UniqueConstraint)

-- Now alter the columns
ALTER TABLE companies ALTER COLUMN country_code NVARCHAR(2);
ALTER TABLE companies ALTER COLUMN default_currency NVARCHAR(3);

ALTER TABLE subscription_plans ALTER COLUMN country_code NVARCHAR(2);
ALTER TABLE subscription_plans ALTER COLUMN currency_code NVARCHAR(3);

ALTER TABLE leads ALTER COLUMN currency_code NVARCHAR(3);

ALTER TABLE developments ALTER COLUMN country_code NVARCHAR(2);

ALTER TABLE properties ALTER COLUMN currency_code NVARCHAR(3);

ALTER TABLE mortgage_applications ALTER COLUMN currency_code NVARCHAR(3);

-- Recreate the UNIQUE constraint on subscription_plans
ALTER TABLE subscription_plans ADD CONSTRAINT UQ_subscription_plans_code_country UNIQUE(code, country_code);
