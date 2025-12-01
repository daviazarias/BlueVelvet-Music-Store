-- ========================================
-- CRIAÇÃO DO BANCO DE DADOS
-- ========================================
CREATE DATABASE IF NOT EXISTS bluevelvet;
USE bluevelvet;

-- ========================================
-- TABELA DE USUÁRIOS
-- ========================================
CREATE TABLE IF NOT EXISTS user
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(255)                                                                       NOT NULL UNIQUE,
    password   VARCHAR(255)                                                                       NOT NULL,
    role       ENUM ('ADMINISTRATOR', 'SALES_MANAGER', 'EDITOR', 'ASSISTANT', 'SHIPPING_MANAGER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ========================================
-- TABELA DE CATEGORIAS
-- ========================================
CREATE TABLE IF NOT EXISTS category
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL UNIQUE,
    image      VARCHAR(255),
    parent_id  BIGINT,
    is_root    BOOLEAN   DEFAULT TRUE,
    enabled    BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ========================================
-- ÍNDICES
-- ========================================
CREATE INDEX idx_user_email ON user (email);
CREATE INDEX idx_category_name ON category (name);
CREATE INDEX idx_category_parent_id ON category (parent_id);
CREATE INDEX idx_category_enabled ON category (enabled);

-- ========================================
-- INSERÇÃO DE DADOS INICIAIS
-- ========================================

-- Usuário Admin Initial (password: 123456789)
INSERT INTO user (email, password, role)
VALUES ('admin@bluevelvet.com', '$2a$10$slYQmyNdGzin7olVN3p5aOYkN/nrsFtoWj9vGXV84EfiVLXrT2qXa', 'ADMINISTRATOR')
ON DUPLICATE KEY UPDATE email=email;

-- Categorias Iniciais (10 categorias raiz)
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
-- EXEMPLO DE SUBCATEGORIAS
-- ========================================
INSERT INTO category (name, image, parent_id, is_root, enabled)
VALUES ('Classic Rock Vinyls', NULL, 2, FALSE, TRUE),
       ('Jazz Vinyls', NULL, 2, FALSE, TRUE),
       ('Rock CDs', NULL, 3, FALSE, TRUE),
       ('Pop CDs', NULL, 3, FALSE, TRUE)
ON DUPLICATE KEY UPDATE name=name;
