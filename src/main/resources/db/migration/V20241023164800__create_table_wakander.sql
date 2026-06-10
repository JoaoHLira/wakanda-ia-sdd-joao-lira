CREATE TABLE wakander (
    id_wakander UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    id_member_kit VARCHAR(255) NOT NULL,
    whatsapp VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status_financeiro VARCHAR(50) NOT NULL DEFAULT 'REGULAR',
    ultima_aula_assistida_datetime TIMESTAMP,
    id_ultima_aula_assistida UUID
);
