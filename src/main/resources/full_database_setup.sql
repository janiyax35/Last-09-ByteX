-- ===================================================================
-- BytEx Customer Care System - Complete Database Setup Script
-- This script will DROP existing tables, CREATE new ones, and INSERT sample data.
-- ===================================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `repair_parts`;
DROP TABLE IF EXISTS `attachments`;
DROP TABLE IF EXISTS `responses`;
DROP TABLE IF EXISTS `part_requests`;
DROP TABLE IF EXISTS `purchase_orders`;
DROP TABLE IF EXISTS `repairs`;
DROP TABLE IF EXISTS `tickets`;
DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `parts`;
DROP TABLE IF EXISTS `suppliers`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ===================================================================
--  TABLE CREATION
-- ===================================================================

CREATE TABLE `users` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `last_login` DATETIME(6),
  `password` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(20),
  `role` VARCHAR(20) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE (`username`),
  UNIQUE (`email`)
);

CREATE TABLE `suppliers` (
  `supplier_id` BIGINT NOT NULL AUTO_INCREMENT,
  `address` TEXT,
  `contact_info` VARCHAR(255),
  `created_at` DATETIME(6) NOT NULL,
  `supplier_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`supplier_id`),
  UNIQUE (`supplier_name`)
);

CREATE TABLE `parts` (
  `part_id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(50) NOT NULL,
  `current_stock` INT NOT NULL,
  `description` TEXT,
  `minimum_stock` INT NOT NULL,
  `part_name` VARCHAR(100) NOT NULL,
  `part_number` VARCHAR(50) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`part_id`),
  UNIQUE (`part_number`)
);

CREATE TABLE `tickets` (
  `ticket_id` BIGINT NOT NULL AUTO_INCREMENT,
  `archived` BOOLEAN NOT NULL,
  `archived_at` DATETIME(6),
  `closed_at` DATETIME(6),
  `created_at` DATETIME(6) NOT NULL,
  `description` TEXT NOT NULL,
  `priority` VARCHAR(10) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `stage` VARCHAR(30) NOT NULL,
  `subject` VARCHAR(100) NOT NULL,
  `updated_at` DATETIME(6),
  `assigned_to_id` BIGINT,
  `customer_id` BIGINT NOT NULL,
  PRIMARY KEY (`ticket_id`),
  FOREIGN KEY (`assigned_to_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `repairs` (
  `repair_id` BIGINT NOT NULL AUTO_INCREMENT,
  `completion_date` DATETIME(6),
  `diagnosis` TEXT NOT NULL,
  `repair_details` TEXT,
  `start_date` DATETIME(6),
  `status` VARCHAR(20) NOT NULL,
  `technician_id` BIGINT NOT NULL,
  `ticket_id` BIGINT NOT NULL,
  PRIMARY KEY (`repair_id`),
  FOREIGN KEY (`technician_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`)
);

CREATE TABLE `part_requests` (
  `request_id` BIGINT NOT NULL AUTO_INCREMENT,
  `fulfillment_date` DATETIME(6),
  `quantity` INT NOT NULL,
  `reason` TEXT,
  `request_date` DATETIME(6),
  `status` VARCHAR(20) NOT NULL,
  `part_id` BIGINT NOT NULL,
  `requestor_id` BIGINT NOT NULL,
  `repair_id` BIGINT,
  PRIMARY KEY (`request_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`),
  FOREIGN KEY (`requestor_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`repair_id`) REFERENCES `repairs` (`repair_id`)
);

CREATE TABLE `responses` (
  `response_id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `message` TEXT NOT NULL,
  `ticket_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`response_id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `attachments` (
  `attachment_id` BIGINT NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(255) NOT NULL,
  `file_size` INT NOT NULL,
  `file_type` VARCHAR(100) NOT NULL,
  `response_id` BIGINT,
  `ticket_id` BIGINT,
  `uploaded_at` DATETIME(6) NOT NULL,
  `uploaded_by` BIGINT NOT NULL,
  PRIMARY KEY (`attachment_id`),
  FOREIGN KEY (`response_id`) REFERENCES `responses` (`response_id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `repair_parts` (
  `part_id` BIGINT NOT NULL,
  `repair_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`part_id`, `repair_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`),
  FOREIGN KEY (`repair_id`) REFERENCES `repairs` (`repair_id`)
);

CREATE TABLE `purchase_orders` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT,
  `actual_delivery` DATETIME(6),
  `expected_delivery` DATETIME(6),
  `order_date` DATETIME(6),
  `status` VARCHAR(20) NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `created_by_id` BIGINT NOT NULL,
  `supplier_id` BIGINT NOT NULL,
  PRIMARY KEY (`order_id`),
  FOREIGN KEY (`created_by_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`)
);

CREATE TABLE `order_items` (
  `order_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`order_id`, `part_id`),
  FOREIGN KEY (`order_id`) REFERENCES `purchase_orders` (`order_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
);

CREATE TABLE `activity_logs` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT,
  `action_type` VARCHAR(50) NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `description` TEXT,
  `entity_id` BIGINT,
  `entity_type` VARCHAR(50) NOT NULL,
  `ip_address` VARCHAR(50),
  `user_id` BIGINT,
  PRIMARY KEY (`log_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
--  SAMPLE DATA INSERTION
-- ===================================================================

INSERT INTO users (username, password, email, full_name, phone_number, role) VALUES
('admin', 'admin123', 'admin@bytex.com', 'System Administrator', '+94711234567', 'ADMIN'),
('john.staff', 'staff123', 'john@bytex.com', 'John Smith', '+94712345678', 'STAFF'),
('sarah.staff', 'staff123', 'sarah@bytex.com', 'Sarah Johnson', '+94713456789', 'STAFF'),
('mike.tech', 'tech123', 'mike@bytex.com', 'Mike Chen', '+94714567890', 'TECHNICIAN'),
('laura.tech', 'tech123', 'laura@bytex.com', 'Laura Silva', '+94715678901', 'TECHNICIAN'),
('david.pm', 'pm123', 'david@bytex.com', 'David Perera', '+94716789012', 'PRODUCT_MANAGER'),
('priya.wm', 'wm123', 'priya@bytex.com', 'Priya Fernando', '+94717890123', 'WAREHOUSE_MANAGER'),
('raj.customer', 'pass123', 'raj@gmail.com', 'Raj Mendis', '+94718901234', 'CUSTOMER'),
('anita.customer', 'pass123', 'anita@outlook.com', 'Anita De Silva', '+94719012345', 'CUSTOMER');

INSERT INTO parts (part_number, part_name, description, current_stock, minimum_stock, unit_price, category, status) VALUES
('CPU001', 'Intel Core i7-12700K', 'High performance CPU', 15, 5, 399.99, 'CPU', 'ACTIVE'),
('RAM001', 'Corsair Vengeance 16GB DDR4', 'High performance RAM', 30, 10, 79.99, 'RAM', 'ACTIVE'),
('GPU001', 'NVIDIA RTX 3080', 'High-end graphics card', 5, 3, 699.99, 'GPU', 'LOW_STOCK'),
('SSD001', 'Samsung 970 EVO 1TB', 'NVMe SSD', 0, 5, 149.99, 'Storage', 'OUT_OF_STOCK');

INSERT INTO tickets (customer_id, subject, description, status, stage, priority, assigned_to_id) VALUES
(8, 'My computer is running slow', 'Ever since the last update, my PC takes forever to boot up.', 'IN_PROGRESS', 'WITH_STAFF', 'MEDIUM', 2),
(9, 'Graphics card not detected', 'My new monitor is blank, and the system does not see my graphics card.', 'IN_PROGRESS', 'WITH_TECHNICIAN', 'HIGH', 3);

INSERT INTO repairs (repair_id, ticket_id, technician_id, diagnosis, status) VALUES
(1, 2, 4, 'Initial diagnosis suggests the GPU may be faulty. Physical inspection required.', 'PENDING');

INSERT INTO part_requests (part_id, requestor_id, quantity, reason, status, repair_id) VALUES
(3, 4, 1, 'Replacement for potentially faulty GPU in ticket #2.', 'PENDING', 1);
