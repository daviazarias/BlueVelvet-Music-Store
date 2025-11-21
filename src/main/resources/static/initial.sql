CREATE DATABASE IF NOT EXISTS bluevelvet;

CREATE TABLE IF NOT EXISTS
    bluevelvet.category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL UNIQUE,
    image VARCHAR(256),
    enabled BOOLEAN DEFAULT TRUE,
    parent_id BIGINT DEFAULT NULL,
    CONSTRAINT category_pk PRIMARY KEY (id),
    CONSTRAINT parent_fk FOREIGN KEY (parent_id) REFERENCES bluevelvet.category(id) ON DELETE RESTRICT
);

INSERT INTO bluevelvet.category(name) 
VALUES ('T-Shirts'), ('Vinyl'), ('CD'), ('MP3'), ('Books'), ('Acoustic Guitar');
