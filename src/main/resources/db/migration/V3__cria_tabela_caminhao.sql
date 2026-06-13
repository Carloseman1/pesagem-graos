CREATE SEQUENCE caminhao_seq START 1 INCREMENT 1;

CREATE TABLE caminhao (
    id BIGINT PRIMARY KEY DEFAULT nextval('caminhao_seq'),
    placa VARCHAR(10) NOT NULL UNIQUE,
    tara DECIMAL(10,2) NOT NULL,
    motorista VARCHAR(255) NOT NULL
);