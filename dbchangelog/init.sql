CREATE DATABASE IF NOT EXISTS `ars_payment` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ars_payment`;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- TABLE: payment_gateway
-- ============================
DROP TABLE IF EXISTS payment_gateway;
CREATE TABLE payment_gateway (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================
-- TABLE: payment_history
-- ============================
DROP TABLE IF EXISTS payment_history;
CREATE TABLE payment_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type INT NOT NULL,
    ref_id INT NOT NULL,
    receiver_id INT NOT NULL,
    user_id INT NOT NULL,
    trans_id VARCHAR(255), -- From BANK
    payment_gateway_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(21, 6) DEFAULT 0.00 NOT NULL,
    info VARCHAR(2000),
    status VARCHAR(20) NOT NULL,
    payment_time TIMESTAMP,
    response VARCHAR(2000),
    error VARCHAR(500),
    description VARCHAR(500) NOT NULL,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================
-- TABLE: outbox
-- ============================
DROP TABLE IF EXISTS outbox;
CREATE TABLE outbox (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ref_id INT NOT NULL,
    type VARCHAR(100) NOT NULL,
    value VARCHAR(1000) NOT NULL,
    error VARCHAR(1000) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_outbox_type_status_id ON outbox (type, status, id DESC);
CREATE INDEX idx_outbox_status_id ON outbox (status, id DESC);

DROP TABLE IF EXISTS balance;
CREATE TABLE balance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ref_id INT NOT NULL,
    type INT NOT NULL,
    balance DECIMAL(21, 6) DEFAULT 0.00 NOT NULL,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


SET FOREIGN_KEY_CHECKS = 1;
