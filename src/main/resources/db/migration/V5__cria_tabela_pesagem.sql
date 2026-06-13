CREATE SEQUENCE pesagem_seq START 1 INCREMENT 1;

CREATE TABLE pesagem (
    id BIGINT PRIMARY KEY DEFAULT nextval('pesagem_seq'),
    placa VARCHAR(10) NOT NULL,
    peso_bruto_estabilizado DECIMAL(10,2) NOT NULL,
    tara DECIMAL(10,2) NOT NULL,
    peso_liquido DECIMAL(10,2) NOT NULL,
    data_hora_pesagem TIMESTAMP NOT NULL,
    custo_da_carga DECIMAL(10,2) NOT NULL,
    balanca_id BIGINT NOT NULL,
    tipo_grao_id BIGINT NOT NULL,
    CONSTRAINT fk_pesagem_balanca FOREIGN KEY (balanca_id) REFERENCES balanca(id),
    CONSTRAINT fk_pesagem_tipo_grao FOREIGN KEY (tipo_grao_id) REFERENCES tipo_grao(id)
);