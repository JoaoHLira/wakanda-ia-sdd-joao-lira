CREATE TABLE historico_relatorio (
    id_relatorio UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    data_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    mensagem TEXT
);
