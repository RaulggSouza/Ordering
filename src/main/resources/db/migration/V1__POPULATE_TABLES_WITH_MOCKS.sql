-- Flyway migration to seed the database with mock data.
-- This migration assumes the schema/tables were created beforehand (e.g. via JPA ddl-auto).

-- Seed UUIDs (for easy reference):
-- customers:
--   0f0b3a9e-7d44-4c1b-9c1e-6a9a2b3c4d5e
--   1a2b3c4d-5e6f-4a1b-8c9d-0e1f2a3b4c5d
-- products:
--   2b3c4d5e-6f70-4b2c-9d0e-1f2a3b4c5d6e
--   3c4d5e6f-7081-4c3d-0e1f-2a3b4c5d6e7f
--   4d5e6f70-8192-4d4e-1f2a-3b4c5d6e7f80
--   0c1d2e3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f (out of stock)
-- orders:
--   5e6f7081-92a3-4e5f-2a3b-4c5d6e7f8091
--   6f708192-a3b4-4f60-3b4c-5d6e7f8091a2
--   b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e (CANCELLED)
--   c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f (SHIPPED)
-- discounts:
--   7f8091a2-b3c4-4a71-4c5d-6e7f8091a2b3 (MINIMUM_VALUE)
--   8a91a2b3-c4d5-4b82-5d6e-7f8091a2b3c4 (MINIMUM_VALUE)
--   9ba2b3c4-d5e6-4c93-6e7f-8091a2b3c4d5 (TIER)
--   a0b1c2d3-e4f5-4da4-7f80-91a2b3c4d5e6 (TIER)
--   d3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f70 (inactive)
--   e4f5a6b7-c8d9-4e0f-1a2b-3c4d5e6f7081 (expired)

-- Customers
INSERT INTO customers (id, name)
VALUES
    ('0f0b3a9e-7d44-4c1b-9c1e-6a9a2b3c4d5e', 'Mock Customer 1'),
    ('1a2b3c4d-5e6f-4a1b-8c9d-0e1f2a3b4c5d', 'Mock Customer 2')
ON CONFLICT (id) DO NOTHING;

-- Products
INSERT INTO products (id, name)
VALUES
    ('2b3c4d5e-6f70-4b2c-9d0e-1f2a3b4c5d6e', 'Mock Product 1'),
    ('3c4d5e6f-7081-4c3d-0e1f-2a3b4c5d6e7f', 'Mock Product 2'),
    ('4d5e6f70-8192-4d4e-1f2a-3b4c5d6e7f80', 'Mock Product 3'),
    ('0c1d2e3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f', 'Mock Product (Out of stock)')
ON CONFLICT (id) DO NOTHING;

-- Product inventory
INSERT INTO product_inventory (product_id, quantity)
VALUES
    ('2b3c4d5e-6f70-4b2c-9d0e-1f2a3b4c5d6e', 100),
    ('3c4d5e6f-7081-4c3d-0e1f-2a3b4c5d6e7f', 50),
    ('4d5e6f70-8192-4d4e-1f2a-3b4c5d6e7f80', 10),
    ('0c1d2e3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f', 0)
ON CONFLICT (product_id) DO NOTHING;

-- Orders
INSERT INTO orders (id, customer_id, street, number, city, state, postal_code, status)
VALUES
    (
        '5e6f7081-92a3-4e5f-2a3b-4c5d6e7f8091',
        '0f0b3a9e-7d44-4c1b-9c1e-6a9a2b3c4d5e',
        'Rua A',
        '123',
        'São Carlos',
        'São Paulo',
        '13560-000',
        'CREATED'
    ),
    (
        '6f708192-a3b4-4f60-3b4c-5d6e7f8091a2',
        '1a2b3c4d-5e6f-4a1b-8c9d-0e1f2a3b4c5d',
        'Rua B',
        '456',
        'São Carlos',
        'São Paulo',
        '13560-001',
        'INVOICED'
    ),
    (
        'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e',
        '0f0b3a9e-7d44-4c1b-9c1e-6a9a2b3c4d5e',
        'Rua C',
        '789',
        'São Carlos',
        'São Paulo',
        '13560-002',
        'CANCELLED'
    ),
    (
        'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f',
        '1a2b3c4d-5e6f-4a1b-8c9d-0e1f2a3b4c5d',
        'Rua D',
        '101',
        'São Carlos',
        'São Paulo',
        '13560-003',
        'SHIPPED'
    )
ON CONFLICT (id) DO NOTHING;

-- Order items (id is generated)
INSERT INTO order_items (product_id, quantity, price, order_id)
VALUES
    (
        '2b3c4d5e-6f70-4b2c-9d0e-1f2a3b4c5d6e',
        2,
        100.00,
        '5e6f7081-92a3-4e5f-2a3b-4c5d6e7f8091'
    ),
    (
        '3c4d5e6f-7081-4c3d-0e1f-2a3b4c5d6e7f',
        1,
        50.00,
        '5e6f7081-92a3-4e5f-2a3b-4c5d6e7f8091'
    ),
    (
        '4d5e6f70-8192-4d4e-1f2a-3b4c5d6e7f80',
        1,
        10.00,
        '6f708192-a3b4-4f60-3b4c-5d6e7f8091a2'
    ),
    (
        '2b3c4d5e-6f70-4b2c-9d0e-1f2a3b4c5d6e',
        1,
        100.00,
        'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e'
    ),
    (
        '3c4d5e6f-7081-4c3d-0e1f-2a3b4c5d6e7f',
        2,
        50.00,
        'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e'
    ),
    (
        '4d5e6f70-8192-4d4e-1f2a-3b4c5d6e7f80',
        3,
        10.00,
        'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f'
    );

-- Discounts
INSERT INTO discounts (id, discount_type, active, expires_at, rule_type, minimum_value, discount_value)
VALUES
    -- MINIMUM_VALUE
    ('7f8091a2-b3c4-4a71-4c5d-6e7f8091a2b3', 'COUPON', TRUE, NULL, 'MINIMUM_VALUE', 200.00, 10.00),
    ('8a91a2b3-c4d5-4b82-5d6e-7f8091a2b3c4', 'SEASONAL', TRUE, NULL, 'MINIMUM_VALUE', 500.00, 15.00),
    -- TIER
    ('9ba2b3c4-d5e6-4c93-6e7f-8091a2b3c4d5', 'CATEGORY', TRUE, NULL, 'TIER', NULL, NULL),
    ('a0b1c2d3-e4f5-4da4-7f80-91a2b3c4d5e6', 'FIRST_PURCHASE', TRUE, NULL, 'TIER', NULL, NULL),
    -- Edge cases
    ('d3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f70', 'COUPON', FALSE, NULL, 'MINIMUM_VALUE', 100.00, 5.00),
    ('e4f5a6b7-c8d9-4e0f-1a2b-3c4d5e6f7081', 'SEASONAL', TRUE, '2000-01-01 00:00:00', 'MINIMUM_VALUE', 150.00, 7.50)
ON CONFLICT (id) DO NOTHING;

-- Discount tiers (id is generated)
INSERT INTO discount_tiers (minimum_value, maximum_value, percentage, discount_id)
VALUES
    -- For 9ba2b3c4-d5e6-4c93-6e7f-8091a2b3c4d5
    (0.00, 199.99, 5.00, '9ba2b3c4-d5e6-4c93-6e7f-8091a2b3c4d5'),
    (200.00, 499.99, 10.00, '9ba2b3c4-d5e6-4c93-6e7f-8091a2b3c4d5'),
    (500.00, 999999.00, 15.00, '9ba2b3c4-d5e6-4c93-6e7f-8091a2b3c4d5'),
    -- For a0b1c2d3-e4f5-4da4-7f80-91a2b3c4d5e6
    (0.00, 99.99, 3.00, 'a0b1c2d3-e4f5-4da4-7f80-91a2b3c4d5e6'),
    (100.00, 299.99, 7.00, 'a0b1c2d3-e4f5-4da4-7f80-91a2b3c4d5e6'),
    (300.00, 999999.00, 12.00, 'a0b1c2d3-e4f5-4da4-7f80-91a2b3c4d5e6');
