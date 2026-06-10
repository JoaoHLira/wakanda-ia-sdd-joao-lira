CREATE TABLE cobranca (
    id_cobranca UUID PRIMARY KEY,
    id_wakander UUID NOT NULL,
    id_payment_asaas VARCHAR(255) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    data_criacao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_wakander FOREIGN KEY (id_wakander) REFERENCES wakander(id_wakander)
);
