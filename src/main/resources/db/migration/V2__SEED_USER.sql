-- Seed a user for local development.
-- email: admin@admin
-- password: admin
INSERT INTO app_users (id, email, password_hash, role, created_at)
VALUES (
    'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0',
    'admin@admin',
    '$2y$10$LpV/EiK74cyTUIPo6iIoiOlj5K//8qJ/UCbb8cG63zqE8QQH8cx4W',
    'ADMIN',
    now()::timestamp
)
ON CONFLICT (email) DO NOTHING;

