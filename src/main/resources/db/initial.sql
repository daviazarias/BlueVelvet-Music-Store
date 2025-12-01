CREATE DATABASE IF NOT EXISTS bluevelvet;

CREATE TABLE IF NOT EXISTS
    bluevelvet.category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL UNIQUE,
    image VARCHAR(256),
    enabled BOOLEAN DEFAULT TRUE,
    parent_id BIGINT DEFAULT NULL,
    is_root BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT category_pk PRIMARY KEY (id),
    CONSTRAINT parent_fk FOREIGN KEY (parent_id) REFERENCES bluevelvet.category(id) ON DELETE RESTRICT,
    CONSTRAINT chk_root CHECK (NOT (is_root AND parent_id IS NOT NULL))
);

INSERT INTO bluevelvet.category(name,is_root)
VALUES ('T-Shirts', FALSE),
       ('Vinyl', FALSE),
       ('CD', FALSE),
       ('MP3', FALSE),
       ('Books', FALSE),
       ('Acoustic Guitar', FALSE),
       ('Guitars', TRUE),
       ('Electric Guitars', FALSE),
       ('Physical Media', TRUE),
       ('Digital Media', TRUE),
       ('Clothes', TRUE);

UPDATE bluevelvet.category SET parent_id = 11 WHERE id = 1;
UPDATE bluevelvet.category SET parent_id = 9 WHERE id = 2;
UPDATE bluevelvet.category SET parent_id = 9 WHERE id = 3;
UPDATE bluevelvet.category SET parent_id = 10 WHERE id = 4;
UPDATE bluevelvet.category SET parent_id = 9 WHERE id = 5;
UPDATE bluevelvet.category SET parent_id = 7 WHERE id = 6;
UPDATE bluevelvet.category SET parent_id = 7 WHERE id = 8;