CREATE SEQUENCE filial_seq START 1 INCREMENT 1;

CREATE TABLE filial (
    id BIGINT PRIMARY KEY DEFAULT nextval('filial_seq'),
    nome VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL
);