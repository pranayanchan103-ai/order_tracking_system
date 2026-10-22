-- Order Tracking System - database schema
-- Run this file FIRST, then seed.sql

DROP DATABASE IF EXISTS order_tracker;
CREATE DATABASE order_tracker
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE order_tracker;

-- One row per order: holds the CURRENT status only
CREATE TABLE orders (
    order_id        VARCHAR(20)  NOT NULL,
    customer_name   VARCHAR(100) NOT NULL,
    product_name    VARCHAR(150) NOT NULL,
    current_status  VARCHAR(30)  NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    CONSTRAINT chk_orders_status CHECK (
        current_status IN ('PLACED', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED')
    )
) ENGINE = InnoDB;

-- Many rows per order: the full journey. Rows are only ever INSERTED, never updated.
CREATE TABLE tracking_history (
    history_id  INT          NOT NULL AUTO_INCREMENT,
    order_id    VARCHAR(20)  NOT NULL,
    status      VARCHAR(30)  NOT NULL,
    location    VARCHAR(150) NOT NULL,
    remarks     VARCHAR(255) NULL,
    event_time  DATETIME     NOT NULL,
    PRIMARY KEY (history_id),
    CONSTRAINT fk_history_order FOREIGN KEY (order_id) REFERENCES orders (order_id),
    CONSTRAINT chk_history_status CHECK (
        status IN ('PLACED', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED')
    ),
    INDEX idx_history_order_time (order_id, event_time)
) ENGINE = InnoDB;
