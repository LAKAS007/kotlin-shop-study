INSERT INTO users (username, email, password_hash, role, created_at)
VALUES ('superadmin', 'superadmin@shop.com', '$2b$12$lZhOPp0rdxDHKTamMClLXeCagqUzzjSMmbMFK3INzuzKM6gMwTgQ2', 'ADMIN', NOW())
ON CONFLICT (email) DO NOTHING;
