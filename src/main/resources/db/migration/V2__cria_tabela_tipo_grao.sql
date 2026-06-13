CREATE SEQUENCE tipo_grao_seq START 1 INCREMENT 1;

CREATE TABLE tipo_grao (
    id BIGINT PRIMARY KEY DEFAULT nextval('tipo_grao_seq'),
    nome VARCHAR(255) NOT NULL UNIQUE,
    preco_comprar_por_tonelada DECIMAL(10,2) NOT NULL,
    estoque_atual_toneladas DECIMAL(10,2) NOT NULL,
    estoque_maximo_toneladas DECIMAL(10,2) NOT NULL
);