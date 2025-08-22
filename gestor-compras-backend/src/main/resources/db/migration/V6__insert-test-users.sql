-- Inserir usuário de teste
-- Email: admin@gestor.com
-- Senha: admin123
-- A senha foi criptografada usando BCrypt com força 12
INSERT INTO users (username, senha, email, role) 
VALUES ('admin', '$2a$12$MnT.Q/GJK1P1GFN6kx2/SOXjKQaJ8CX8QXnVi1xgBQGBj9qLkH8lK', 'admin@gestor.com', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- Inserir usuário comum de teste
-- Email: user@gestor.com  
-- Senha: user123
INSERT INTO users (username, senha, email, role)
VALUES ('user', '$2a$12$Ey1UYKNPyF1X4/jYxzOJRe4dQ7LJk3qVrLbZpZ8w1eKJ2mQ9hFvZy', 'user@gestor.com', 'USER')
ON CONFLICT (email) DO NOTHING;

