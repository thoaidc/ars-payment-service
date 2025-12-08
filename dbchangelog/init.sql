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
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
