CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_id VARCHAR(36), -- UUID para vincular las dos partes de una transferencia
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- 'DEPOSIT' o 'WITHDRAWAL'
    amount DECIMAL(19, 4) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255)
);
