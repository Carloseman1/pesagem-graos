CREATE SEQUENCE transacao_seq START 1 INCREMENT 1;

CREATE TABLE transacao_transporte (
    id BIGINT PRIMARY KEY DEFAULT nextval('transacao_seq'),
    status VARCHAR(20) NOT NULL,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP,
    caminhao_id BIGINT NOT NULL,
    tipo_grao_id BIGINT NOT NULL,
    filial_id BIGINT NOT NULL,
    pesagem_id BIGINT,
    CONSTRAINT fk_transacao_caminhao FOREIGN KEY (caminhao_id) REFERENCES caminhao(id),
    CONSTRAINT fk_transacao_tipo_grao FOREIGN KEY (tipo_grao_id) REFERENCES tipo_grao(id),
    CONSTRAINT fk_transacao_filial FOREIGN KEY (filial_id) REFERENCES filial(id),
    CONSTRAINT fk_transacao_pesagem FOREIGN KEY (pesagem_id) REFERENCES pesagem(id)
);