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
    type VARCHAR(100) NOT NULL,
    ref_id INT NOT NULL,
    trans_id VARCHAR(255) NOT NULL,
    payment_gateway_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_time TIMESTAMP,
    amount DECIMAL(21, 6) DEFAULT 0.00,
    info VARCHAR(2000) NOT NULL,
    status VARCHAR(20) NOT NULL,
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
    saga_id VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    value VARCHAR(1000) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_outbox_type_status_id ON outbox (type, status, id DESC);
CREATE INDEX idx_outbox_status_id ON outbox (status, id DESC);


SET FOREIGN_KEY_CHECKS = 1;
