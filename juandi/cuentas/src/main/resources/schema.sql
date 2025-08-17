CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type VARCHAR(50) NOT NULL,
    balance DECIMAL(19, 4) NOT NULL,
    status VARCHAR(20) NOT NULL,
    bank_id BIGINT NOT NULL -- ID del banco en el microservicio externo de bancos
);
