-- ========================================
-- SCRIPT DE INSERÇÃO DE DADOS INICIAIS
-- BlueVelvet Music Store
-- ========================================

USE bluevelvet;

-- ========================================
-- USUÁRIO ADMIN
-- ========================================
-- Email: admin@bluevelvet.com
-- Senha: 123456789
INSERT INTO `user` (email, password, role)
VALUES ('admin@bluevelvet.com', '$2a$10$slYQmyNdGzin7olVN3p5aOYkN/nrsFtoWj9vGXV84EfiVLXrT2qXa', 'ADMINISTRATOR')
ON DUPLICATE KEY UPDATE email=email;

-- ========================================
-- CATEGORIAS RAIZ (10 categorias principais)
-- ========================================
INSERT INTO category (name, image, parent_id, is_root, enabled)
VALUES ('T-Shirts', 'tshirts.jpg', NULL, TRUE, TRUE),
       ('Vinyl', 'vinyl.jpg', NULL, TRUE, TRUE),
       ('CDs', 'cds.jpg', NULL, TRUE, TRUE),
       ('MP3', 'mp3.jpg', NULL, TRUE, TRUE),
       ('Books', 'books.jpg', NULL, TRUE, TRUE),
       ('Acoustic Guitar', 'acoustic-guitar.jpg', NULL, TRUE, TRUE),
       ('Electric Guitar', 'electric-guitar.jpg', NULL, TRUE, TRUE),
       ('Bass', 'bass.jpg', NULL, TRUE, TRUE),
       ('Drums', 'drums.jpg', NULL, TRUE, TRUE),
       ('Accessories', 'accessories.jpg', NULL, TRUE, TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- ========================================
-- SUBCATEGORIAS (filhas das categorias raiz)
-- ========================================
INSERT INTO category (name, image, parent_id, is_root, enabled)
VALUES ('Classic Rock Vinyls', NULL, 2, FALSE, TRUE),
       ('Jazz Vinyls', NULL, 2, FALSE, TRUE),
       ('Rock CDs', NULL, 3, FALSE, TRUE),
       ('Pop CDs', NULL, 3, FALSE, TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- ========================================
-- VERIFICAÇÃO DOS DADOS INSERIDOS
-- ========================================
SELECT 'USUÁRIOS CADASTRADOS:' as '';
SELECT id, email, role
FROM `user`;

SELECT 'CATEGORIAS RAIZ:' as '';
SELECT id, name, is_root, enabled
FROM category
WHERE is_root = TRUE;

SELECT 'SUBCATEGORIAS:' as '';
SELECT id, name, parent_id, is_root
FROM category
WHERE is_root = FALSE;

