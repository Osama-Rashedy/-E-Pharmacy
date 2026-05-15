-- ═══════════════════════════════════════════════════════════════════
--  Smart E-Pharmacy System — MySQL Schema
--  Run this manually OR let DatabaseInitializer auto-create it.
-- ═══════════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS epharmacy
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE epharmacy;

CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,           -- BCrypt hash
    role       ENUM('ADMIN','PHARMACIST','PATIENT') NOT NULL,
    phone      VARCHAR(20),
    address    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medicines (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    name                  VARCHAR(150) NOT NULL,
    category              VARCHAR(100),
    price                 DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantity              INT NOT NULL DEFAULT 0,
    expiry_date           DATE,
    manufacturer          VARCHAR(150),
    requires_prescription BOOLEAN NOT NULL DEFAULT FALSE,
    description           TEXT,
    image_path            VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS orders (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    payment_method VARCHAR(50),
    status         ENUM('PENDING','APPROVED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    order_id    INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity    INT NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id)    REFERENCES orders(id)    ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS prescriptions (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    patient_id  INT NOT NULL,
    order_id    INT,
    image_path  VARCHAR(500),
    status      ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    notes       TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (order_id)   REFERENCES orders(id)  ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT,
    message    TEXT NOT NULL,
    type       VARCHAR(50),
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── Indexes ──────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_users_email    ON users(email);
CREATE INDEX IF NOT EXISTS idx_orders_patient ON orders(patient_id);
CREATE INDEX IF NOT EXISTS idx_medicines_name ON medicines(name);
CREATE INDEX IF NOT EXISTS idx_prescriptions_status ON prescriptions(status);
