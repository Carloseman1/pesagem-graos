CREATE SEQUENCE balanca_seq START 1 INCREMENT 1;

CREATE TABLE balanca (
    id BIGINT PRIMARY KEY DEFAULT nextval('balanca_seq'),
    identificador VARCHAR(100) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    filial_id BIGINT NOT NULL,
    CONSTRAINT fk_balanca_filial FOREIGN KEY (filial_id) REFERENCES filial(id)
);