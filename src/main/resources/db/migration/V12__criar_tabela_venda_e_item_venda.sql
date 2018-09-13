CREATE TABLE venda (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    data_criacao DATETIME NOT NULL,
    valor_frete DECIMAL(10,2),
    valor_desconto DECIMAL(10,2),
    valor_total DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    observacao VARCHAR(200),
    data_entrega DATETIME,
    cliente_id BIGINT(20) NOT NULL,
    usuario_id BIGINT(20) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE item_venda (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    quantidade INTEGER NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL,
    cerveja_id BIGINT(20) NOT NULL,
    venda_id BIGINT(20) NOT NULL,
    FOREIGN KEY (cerveja_id) REFERENCES cerveja(id),
    FOREIGN KEY (venda_id) REFERENCES venda(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;