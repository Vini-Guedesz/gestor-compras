-- Migration para atualizar roles de usuários existentes
-- Substitui a role "USER" por "USUARIO" para manter compatibilidade com novo enum

-- Atualiza todos os usuários que têm role "USER" para "USUARIO"
UPDATE users
SET role = 'USUARIO'
WHERE role = 'USER';

-- Comentário: As novas roles COMPRADOR e APROVADOR estarão disponíveis para atribuição futura
-- através do endpoint de atualização de roles (/api/v1/users/role)
