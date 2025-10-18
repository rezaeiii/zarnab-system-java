-- Add state column to ingots table
ALTER TABLE ingots
ADD COLUMN state VARCHAR(255) DEFAULT 'GENERATED' NOT NULL;

-- Create ingot_batches table
CREATE TABLE ingot_batches (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    manufacture_date DATE NOT NULL
);

-- Add batch_id column to ingots table
ALTER TABLE ingots
ADD COLUMN batch_id BIGINT;

-- Add foreign key constraint to ingots table
ALTER TABLE ingots
ADD CONSTRAINT fk_ingot_batch
    FOREIGN KEY (batch_id)
    REFERENCES ingot_batches(id);

-- Add check constraint for state column in ingots table
ALTER TABLE ingots
ADD CONSTRAINT chk_ingot_state
    CHECK (state IN ('GENERATED', 'ASSIGNED'));

-- =====================================================================================
-- UPDATE SCRIPT FOR EXISTING INGOTS
-- This script updates old ingot serial numbers to the new naming convention.
--
-- IMPORTANT:
-- 1. Run this script only ONCE after the schema changes have been applied.
-- 2. This script uses a window function to generate a new monthly sequence.
--    The ordering is based on the ingot's 'id', which usually reflects creation order.
-- 3. Product codes for coins (COIN_FULL, COIN_HALF, etc.) cannot be determined
--    from weight alone. Ingots that cannot be mapped will have a 'D' (Default)
--    placeholder in the serial and must be updated manually if needed.
-- 4. It is highly recommended to BACK UP your 'ingots' table before running this script.
-- =====================================================================================

WITH new_serials AS (
    SELECT
        id,
        (
            CASE
                WHEN weight_grams = 200.0 THEN 'C'
                WHEN weight_grams = 100.0 THEN 'E'
                WHEN weight_grams = 50.0  THEN 'G'
                WHEN weight_grams = 20.0  THEN 'I'
                WHEN weight_grams = 10.0  THEN 'K'
                WHEN weight_grams = 5.0   THEN 'M'
                WHEN weight_grams = 2.5   THEN 'O'
                WHEN weight_grams = 1.0   THEN 'Q'
                ELSE 'D' -- Default for unmapped weights
            END
        ) ||
        (
            CASE
                WHEN karat = 995 THEN 'A'
                WHEN karat = 750 THEN 'B'
                ELSE 'C' -- Default for unmapped karats
            END
        ) ||
        TO_CHAR(manufacture_date, 'YYMM') ||
        LPAD(
            (ROW_NUMBER() OVER(PARTITION BY EXTRACT(YEAR FROM manufacture_date), EXTRACT(MONTH FROM manufacture_date) ORDER BY id))::TEXT,
            4,
            '0'
        ) AS new_serial
    FROM
        ingots
)
UPDATE ingots
SET serial = new_serials.new_serial
FROM new_serials
WHERE ingots.id = new_serials.id;
