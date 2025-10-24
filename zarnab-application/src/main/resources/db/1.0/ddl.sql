-- =====================================================================================
-- DATA SEEDING SCRIPT (V4 - Declarative & Idempotent)
-- This script uses pure SQL without procedural blocks to be compatible with Spring Boot's initializer.
-- It is designed to be safe to run multiple times.
-- =====================================================================================

-- Step 1: Create Users (safe to re-run)
INSERT INTO users (mobile_number, profile_type, enabled, first_name, last_name, national_id, created_at, updated_at)
VALUES
    ('09999999999', 'NATURAL', true, 'ادمین', 'سیستم', '0000000000', NOW(), NOW()),
    ('09999999998', 'NATURAL', true, 'کانتر', 'تستی', '2222222222', NOW(), NOW()),
    ('09999999997', 'NATURAL', true, 'مشتری', 'تستی', '0000000001', NOW(), NOW())
ON CONFLICT (mobile_number) DO NOTHING;

-- Step 2: Create a new Ingot Batch for this run
INSERT INTO ingot_batches (manufacture_date, created_at, updated_at)
VALUES (CURRENT_DATE, NOW(), NOW());

-- Step 3: Create 3000 Ingots (safe to re-run)
-- This will only insert ingots whose serial numbers do not already exist.
WITH user_ids AS (
    SELECT id AS admin_id FROM users WHERE mobile_number = '09999999999'
),
batch_info AS (
    SELECT id AS batch_id FROM ingot_batches ORDER BY created_at DESC LIMIT 1
)
INSERT INTO ingots (serial, manufacture_date, karat, weight_grams, state, batch_id, owner_id, created_at, updated_at)
SELECT
    'C' || 'A' || TO_CHAR(CURRENT_DATE, 'YYMM') || LPAD(i::TEXT, 4, '0') AS serial,
    CURRENT_DATE, 995, 1.0, 'ASSIGNED',
    b.batch_id, u.admin_id, NOW(), NOW()
FROM generate_series(1, 3000) AS i, user_ids u, batch_info b
ON CONFLICT (serial) DO NOTHING;

-- Step 4: Create transfer records for the new batch (safe to re-run)
-- This only creates transfers for ingots that don't have transfers yet.
WITH new_ingots AS (
    SELECT i.id FROM ingots i
    LEFT JOIN transfers t ON i.id = t.ingot_id
    WHERE i.batch_id = (SELECT id FROM ingot_batches ORDER BY created_at DESC LIMIT 1)
    GROUP BY i.id
    HAVING COUNT(t.id) = 0
),
user_ids AS (
    SELECT
        (SELECT id FROM users WHERE mobile_number = '09999999999') AS admin_id,
        (SELECT id FROM users WHERE mobile_number = '09999999998') AS counter_id,
        (SELECT id FROM users WHERE mobile_number = '09999999997') AS customer_id
)
INSERT INTO transfers (ingot_id, seller_id, buyer_id, status, created_at, updated_at)
-- Admin -> Counter Transfers
SELECT ni.id, u.admin_id, u.counter_id, 'COMPLETED', NOW(), NOW() FROM new_ingots ni, user_ids u
UNION ALL
-- Counter -> Customer Transfers
SELECT ni.id, u.counter_id, u.customer_id, 'COMPLETED', NOW(), NOW() FROM new_ingots ni, user_ids u;

-- Step 5: Update final owner for the new batch to customer (safe to re-run)
UPDATE ingots
SET owner_id = (SELECT id FROM users WHERE mobile_number = '09999999997')
WHERE batch_id = (SELECT id FROM ingot_batches ORDER BY created_at DESC LIMIT 1);

WITH ingots_to_report AS (
    SELECT i.id, ROW_NUMBER() OVER (ORDER BY i.id) as rn
    FROM ingots i
    LEFT JOIN theft_report tr ON i.id = tr.ingot_id
--     WHERE i.batch_id = (SELECT id FROM ingot_batches ORDER BY created_at DESC LIMIT 1)
--     AND tr.id IS NULL
    LIMIT 300
)
INSERT INTO theft_report (ingot_id, reporter_id, type, description, status, created_at, updated_at)
SELECT
    itr.id,
    (SELECT id FROM users WHERE mobile_number = '09999999997'),
    (ARRAY['THEFT', 'MISSING', 'TAMPERING'])[(itr.rn % 3) + 1],
    'Automated report for ingot ' || itr.id,
    (ARRAY['PENDING', 'APPROVED', 'REJECTED'])[(itr.rn % 3) + 1],
    NOW(),
    NOW()
FROM ingots_to_report itr;
