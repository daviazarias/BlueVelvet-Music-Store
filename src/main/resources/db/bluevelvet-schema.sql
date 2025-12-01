-- ========================================
-- CRIAÇÃO DO BANCO DE DADOS
-- ========================================
CREATE DATABASE IF NOT EXISTS bluevelvet;
USE bluevelvet;

-- ========================================
-- TABELA DE USUÁRIOS
-- ========================================
CREATE TABLE IF NOT EXISTS `user`
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(255)                                                                       NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
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
-- TABELA DE PRODUTOS
-- ========================================
CREATE TABLE IF NOT EXISTS product
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(255)   NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2) NOT NULL,
    stock_quantity INT            NOT NULL,
    image          VARCHAR(255),
    category_id    BIGINT         NOT NULL,
    enabled        BOOLEAN   DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE,
    INDEX idx_product_category (category_id),
    INDEX idx_product_enabled (enabled)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ========================================
-- ÍNDICES
-- ========================================
CREATE INDEX idx_user_email ON `user` (email);
CREATE INDEX idx_category_name ON category (name);
CREATE INDEX idx_category_parent_id ON category (parent_id);
CREATE INDEX idx_category_enabled ON category (enabled);

-- ========================================
-- INSERÇÃO DE DADOS INICIAIS
-- ========================================

-- Usuário Admin Initial (password: 123456789)
INSERT INTO `user` (email, name, password, role)
VALUES ('admin@bluevelvet.com', 'Administrador', '$2a$10$slYQmyNdGzin7olVN3p5aOYkN/nrsFtoWj9vGXV84EfiVLXrT2qXa',
        'ADMINISTRATOR')
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

-- Exemplo de Subcategorias
INSERT INTO category (name, image, parent_id, is_root, enabled)
VALUES ('Classic Rock Vinyls', NULL, 2, FALSE, TRUE),
       ('Jazz Vinyls', NULL, 2, FALSE, TRUE),
       ('Rock CDs', NULL, 3, FALSE, TRUE),
       ('Pop CDs', NULL, 3, FALSE, TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- ========================================
-- PRODUTOS PARA TESTE DE PAGINAÇÃO
-- ========================================

-- Produtos em T-Shirts (Categoria 1)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Camiseta Rock Clássico', 'Camiseta com estampa de banda clássica', 49.99, 50, 1, TRUE),
       ('Camiseta Metal', 'Camiseta com logo de banda metal', 49.99, 30, 1, TRUE),
       ('Camiseta Jazz', 'Camiseta com tema jazz', 49.99, 25, 1, TRUE),
       ('Camiseta Blues', 'Camiseta com tema blues', 49.99, 40, 1, TRUE),
       ('Camiseta Reggae', 'Camiseta com tema reggae', 49.99, 35, 1, TRUE),
       ('Camiseta Punk', 'Camiseta com tema punk', 49.99, 20, 1, TRUE),
       ('Camiseta Hip Hop', 'Camiseta com tema hip hop', 49.99, 45, 1, TRUE),
       ('Camiseta Eletrônica', 'Camiseta com tema eletrônico', 49.99, 38, 1, TRUE),
       ('Camiseta Folk', 'Camiseta com tema folk', 49.99, 22, 1, TRUE),
       ('Camiseta Samba', 'Camiseta com tema samba', 49.99, 33, 1, TRUE),
       ('Camiseta Forró', 'Camiseta com tema forró', 49.99, 28, 1, TRUE),
       ('Camiseta Sertanejo', 'Camiseta com tema sertanejo', 49.99, 41, 1, TRUE);

-- Produtos em Vinyl (Categoria 2)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Vinil The Beatles - Abbey Road', 'Álbum clássico em vinil', 89.99, 15, 2, TRUE),
       ('Vinil Pink Floyd - The Wall', 'Álbum duplo em vinil', 99.99, 12, 2, TRUE),
       ('Vinil Led Zeppelin IV', 'Clássico do rock em vinil', 89.99, 10, 2, TRUE),
       ('Vinil David Bowie - Ziggy Stardust', 'Álbum icônico em vinil', 89.99, 8, 2, TRUE),
       ('Vinil Queen - A Night at the Opera', 'Álbum de estúdio em vinil', 89.99, 14, 2, TRUE),
       ('Vinil The Rolling Stones - Exile', 'Álbum clássico em vinil', 89.99, 11, 2, TRUE),
       ('Vinil Jimi Hendrix - Are You Experienced', 'Álbum de estreia em vinil', 89.99, 9, 2, TRUE),
       ('Vinil Black Sabbath - Paranoid', 'Álbum de metal em vinil', 89.99, 13, 2, TRUE),
       ('Vinil The Who - Tommy', 'Ópera rock em vinil', 99.99, 7, 2, TRUE),
       ('Vinil Fleetwood Mac - Rumours', 'Álbum de sucesso em vinil', 89.99, 16, 2, TRUE),
       ('Vinil Nirvana - Nevermind', 'Álbum grunge em vinil', 89.99, 12, 2, TRUE),
       ('Vinil Metallica - Master of Puppets', 'Álbum de metal em vinil', 89.99, 10, 2, TRUE);

-- Produtos em CDs (Categoria 3)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('CD The Beatles - Abbey Road', 'Álbum clássico em CD', 39.99, 30, 3, TRUE),
       ('CD Pink Floyd - The Wall', 'Álbum duplo em CD', 49.99, 25, 3, TRUE),
       ('CD Led Zeppelin IV', 'Clássico do rock em CD', 39.99, 20, 3, TRUE),
       ('CD David Bowie - Ziggy Stardust', 'Álbum icônico em CD', 39.99, 18, 3, TRUE),
       ('CD Queen - A Night at the Opera', 'Álbum de estúdio em CD', 39.99, 28, 3, TRUE),
       ('CD The Rolling Stones - Exile', 'Álbum clássico em CD', 39.99, 22, 3, TRUE),
       ('CD Jimi Hendrix - Are You Experienced', 'Álbum de estreia em CD', 39.99, 19, 3, TRUE),
       ('CD Black Sabbath - Paranoid', 'Álbum de metal em CD', 39.99, 26, 3, TRUE),
       ('CD The Who - Tommy', 'Ópera rock em CD', 49.99, 15, 3, TRUE),
       ('CD Fleetwood Mac - Rumours', 'Álbum de sucesso em CD', 39.99, 32, 3, TRUE),
       ('CD Nirvana - Nevermind', 'Álbum grunge em CD', 39.99, 24, 3, TRUE),
       ('CD Metallica - Master of Puppets', 'Álbum de metal em CD', 39.99, 21, 3, TRUE);

-- Produtos em MP3 (Categoria 4)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Álbum MP3 - The Beatles Collection', 'Coleção completa em MP3', 9.99, 100, 4, TRUE),
       ('Álbum MP3 - Pink Floyd Essentials', 'Essenciais do Pink Floyd em MP3', 9.99, 95, 4, TRUE),
       ('Álbum MP3 - Led Zeppelin Greatest Hits', 'Maiores sucessos em MP3', 9.99, 90, 4, TRUE),
       ('Álbum MP3 - David Bowie Classics', 'Clássicos de Bowie em MP3', 9.99, 88, 4, TRUE),
       ('Álbum MP3 - Queen Anthology', 'Antologia do Queen em MP3', 9.99, 92, 4, TRUE),
       ('Álbum MP3 - Rolling Stones Collection', 'Coleção dos Stones em MP3', 9.99, 87, 4, TRUE),
       ('Álbum MP3 - Jimi Hendrix Experience', 'Experiência Hendrix em MP3', 9.99, 85, 4, TRUE),
       ('Álbum MP3 - Black Sabbath Classics', 'Clássicos do Sabbath em MP3', 9.99, 89, 4, TRUE),
       ('Álbum MP3 - The Who Hits', 'Sucessos do The Who em MP3', 9.99, 91, 4, TRUE),
       ('Álbum MP3 - Fleetwood Mac Best', 'Melhores do Fleetwood em MP3', 9.99, 93, 4, TRUE),
       ('Álbum MP3 - Nirvana Collection', 'Coleção do Nirvana em MP3', 9.99, 86, 4, TRUE),
       ('Álbum MP3 - Metallica Anthology', 'Antologia do Metallica em MP3', 9.99, 84, 4, TRUE);

-- Produtos em Books (Categoria 5)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Livro - The Beatles Story', 'História completa dos Beatles', 79.99, 20, 5, TRUE),
       ('Livro - Pink Floyd: A Jornada', 'Jornada musical do Pink Floyd', 69.99, 18, 5, TRUE),
       ('Livro - Led Zeppelin: Lendas do Rock', 'Lendas do rock em detalhes', 79.99, 15, 5, TRUE),
       ('Livro - David Bowie: Starman', 'Biografia de David Bowie', 69.99, 16, 5, TRUE),
       ('Livro - Queen: The Show Must Go On', 'História do Queen', 79.99, 19, 5, TRUE),
       ('Livro - Rolling Stones: Vidas Selvagens', 'Vidas dos Stones', 69.99, 17, 5, TRUE),
       ('Livro - Jimi Hendrix: Gênio do Violão', 'Vida de Jimi Hendrix', 79.99, 14, 5, TRUE),
       ('Livro - Black Sabbath: Paranoia', 'História do Black Sabbath', 69.99, 13, 5, TRUE),
       ('Livro - The Who: Gerações', 'Gerações do The Who', 79.99, 12, 5, TRUE),
       ('Livro - Fleetwood Mac: Rumores', 'Histórias do Fleetwood Mac', 69.99, 18, 5, TRUE),
       ('Livro - Nirvana: Smells Like Teen Spirit', 'História do Nirvana', 79.99, 16, 5, TRUE),
       ('Livro - Metallica: Através da Escuridão', 'Jornada do Metallica', 69.99, 15, 5, TRUE);

-- Produtos em Acoustic Guitar (Categoria 6)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Violão Acústico Yamaha C40', 'Violão clássico profissional', 299.99, 10, 6, TRUE),
       ('Violão Acústico Takamine GD30', 'Violão de aço de qualidade', 399.99, 8, 6, TRUE),
       ('Violão Acústico Fender CD-60S', 'Violão acústico versátil', 349.99, 12, 6, TRUE),
       ('Violão Acústico Ibanez PC12E', 'Violão eletroacústico', 449.99, 6, 6, TRUE),
       ('Violão Acústico Epiphone DR-100', 'Violão de entrada', 249.99, 15, 6, TRUE),
       ('Violão Acústico Martin D-28', 'Violão profissional premium', 1299.99, 3, 6, TRUE),
       ('Violão Acústico Taylor 110', 'Violão de qualidade', 499.99, 7, 6, TRUE),
       ('Violão Acústico Washburn D10S', 'Violão dreadnought', 379.99, 9, 6, TRUE),
       ('Violão Acústico Seagull S6', 'Violão canadense', 429.99, 5, 6, TRUE),
       ('Violão Acústico Cort AF510', 'Violão folk', 359.99, 11, 6, TRUE),
       ('Violão Acústico Ovation CC24', 'Violão de corpo redondo', 549.99, 4, 6, TRUE),
       ('Violão Acústico Breedlove Discovery', 'Violão artesanal', 599.99, 6, 6, TRUE);

-- Produtos em Electric Guitar (Categoria 7)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Guitarra Elétrica Fender Stratocaster', 'Clássica Stratocaster', 799.99, 8, 7, TRUE),
       ('Guitarra Elétrica Gibson Les Paul', 'Lendária Les Paul', 999.99, 5, 7, TRUE),
       ('Guitarra Elétrica Ibanez RG550', 'Guitarra de shred', 549.99, 12, 7, TRUE),
       ('Guitarra Elétrica Epiphone SG', 'SG de qualidade', 399.99, 10, 7, TRUE),
       ('Guitarra Elétrica Squier Stratocaster', 'Stratocaster de entrada', 299.99, 18, 7, TRUE),
       ('Guitarra Elétrica PRS SE Custom', 'Guitarra versátil', 649.99, 7, 7, TRUE),
       ('Guitarra Elétrica Jackson Dinky', 'Guitarra de metal', 499.99, 9, 7, TRUE),
       ('Guitarra Elétrica Schecter Omen', 'Guitarra acessível', 379.99, 11, 7, TRUE),
       ('Guitarra Elétrica Yamaha Pacifica', 'Guitarra versátil', 449.99, 13, 7, TRUE),
       ('Guitarra Elétrica Cort X2', 'Guitarra de entrada', 349.99, 14, 7, TRUE),
       ('Guitarra Elétrica Gretsch G5422', 'Guitarra semi-hollow', 699.99, 6, 7, TRUE),
       ('Guitarra Elétrica Fender Telecaster', 'Clássica Telecaster', 799.99, 7, 7, TRUE);

-- Produtos em Bass (Categoria 8)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Baixo Fender Precision Bass', 'Clássico Precision Bass', 699.99, 6, 8, TRUE),
       ('Baixo Fender Jazz Bass', 'Lendário Jazz Bass', 699.99, 5, 8, TRUE),
       ('Baixo Ibanez SR500', 'Baixo moderno', 549.99, 8, 8, TRUE),
       ('Baixo Epiphone EB-3', 'Baixo SG-style', 349.99, 10, 8, TRUE),
       ('Baixo Squier Precision Bass', 'Precision de entrada', 249.99, 14, 8, TRUE),
       ('Baixo Yamaha TRBX174', 'Baixo versátil', 399.99, 9, 8, TRUE),
       ('Baixo Cort GB34A', 'Baixo de qualidade', 449.99, 7, 8, TRUE),
       ('Baixo Schecter Stiletto', 'Baixo de metal', 499.99, 6, 8, TRUE),
       ('Baixo Warwick Rockbass', 'Baixo profissional', 649.99, 4, 8, TRUE),
       ('Baixo Spector NS Etna', 'Baixo premium', 799.99, 3, 8, TRUE),
       ('Baixo Lakland 44-64', 'Baixo custom', 899.99, 2, 8, TRUE),
       ('Baixo Musicman StingRay', 'Baixo lendário', 749.99, 5, 8, TRUE);

-- Produtos em Drums (Categoria 9)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Bateria Ludwig Classic Maple', 'Bateria profissional', 1999.99, 2, 9, TRUE),
       ('Bateria Pearl Masters Maple', 'Bateria de qualidade', 1799.99, 3, 9, TRUE),
       ('Bateria Yamaha Stage Custom', 'Bateria versátil', 999.99, 5, 9, TRUE),
       ('Bateria Gretsch USA Custom', 'Bateria profissional', 2199.99, 2, 9, TRUE),
       ('Bateria Sonor SQ2', 'Bateria premium', 1899.99, 2, 9, TRUE),
       ('Bateria Tama Starclassic', 'Bateria de estúdio', 1699.99, 3, 9, TRUE),
       ('Bateria Mapex Armory', 'Bateria intermediária', 799.99, 6, 9, TRUE),
       ('Bateria Ddrum D2', 'Bateria de entrada', 599.99, 8, 9, TRUE),
       ('Bateria PDP Concept Maple', 'Bateria de qualidade', 1299.99, 4, 9, TRUE),
       ('Bateria Vic Firth Drum Kit', 'Kit completo', 699.99, 5, 9, TRUE),
       ('Bateria Canopus Yaiba', 'Bateria premium', 2299.99, 1, 9, TRUE),
       ('Bateria Fibes Fiberglass', 'Bateria vintage', 1599.99, 2, 9, TRUE);

-- Produtos em Accessories (Categoria 10)
INSERT INTO product (name, description, price, stock_quantity, category_id, enabled)
VALUES ('Cabo de Guitarra 3m', 'Cabo de qualidade', 29.99, 50, 10, TRUE),
       ('Correia de Guitarra', 'Correia confortável', 39.99, 40, 10, TRUE),
       ('Palhetas Dunlop (12 unidades)', 'Palhetas de qualidade', 9.99, 100, 10, TRUE),
       ('Afinador Digital', 'Afinador preciso', 49.99, 30, 10, TRUE),
       ('Suporte de Guitarra', 'Suporte resistente', 59.99, 25, 10, TRUE),
       ('Capotraste', 'Capotraste de qualidade', 19.99, 60, 10, TRUE),
       ('Correia de Baixo', 'Correia para baixo', 39.99, 35, 10, TRUE),
       ('Estojo de Guitarra', 'Estojo rígido', 149.99, 15, 10, TRUE),
       ('Amplificador Portátil', 'Amp de prática', 199.99, 10, 10, TRUE),
       ('Pedal de Distorção', 'Efeito de distorção', 99.99, 20, 10, TRUE),
       ('Metrônomo Digital', 'Metrônomo preciso', 39.99, 25, 10, TRUE),
       ('Microfone Dinâmico', 'Microfone profissional', 149.99, 12, 10, TRUE);
