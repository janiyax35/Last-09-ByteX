-- ===================================================================
-- ByteX Customer Care System - Full Database Setup
-- Database: final_bytex_customer_care_system
-- This script uses ENUM ORDINAL VALUES (integers) for all enum fields.
-- ===================================================================

-- Drop existing tables in reverse order of dependency
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `purchase_orders`;
DROP TABLE IF EXISTS `supplier_parts`;
DROP TABLE IF EXISTS `part_requests`;
DROP TABLE IF EXISTS `repair_parts`;
DROP TABLE IF EXISTS `repairs`;
DROP TABLE IF EXISTS `responses`;
DROP TABLE IF EXISTS `tickets`;
DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `suppliers`;
DROP TABLE IF EXISTS `parts`;
DROP TABLE IF EXISTS `users`;

-- ===================================================================
-- Table Creation
-- ===================================================================

-- Users Table
CREATE TABLE `users` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role` tinyint NOT NULL COMMENT '0=CUSTOMER, 1=STAFF, 2=TECHNICIAN, 3=PRODUCT_MANAGER, 4=WAREHOUSE_MANAGER, 5=ADMIN',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_username` (`username`),
  UNIQUE KEY `UK_email` (`email`)
) ENGINE=InnoDB;

-- Parts Table
CREATE TABLE `parts` (
  `part_id` BIGINT NOT NULL AUTO_INCREMENT,
  `part_number` varchar(50) NOT NULL,
  `part_name` varchar(100) NOT NULL,
  `description` text,
  `current_stock` int NOT NULL DEFAULT '0',
  `minimum_stock` int NOT NULL DEFAULT '5',
  `unit_price` decimal(10,2) NOT NULL,
  `category` varchar(50) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=ACTIVE, 1=LOW_STOCK, 2=OUT_OF_STOCK, 3=DISCONTINUED',
  PRIMARY KEY (`part_id`),
  UNIQUE KEY `UK_part_number` (`part_number`)
) ENGINE=InnoDB;

-- Suppliers Table
CREATE TABLE `suppliers` (
  `supplier_id` BIGINT NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) NOT NULL,
  `contact_info` varchar(255) DEFAULT NULL,
  `address` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB;

-- Supplier_Parts Junction Table
CREATE TABLE `supplier_parts` (
  `supplier_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  PRIMARY KEY (`supplier_id`, `part_id`),
  KEY `fk_supplierparts_part` (`part_id`),
  CONSTRAINT `fk_supplierparts_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  CONSTRAINT `fk_supplierparts_part` FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
) ENGINE=InnoDB;

-- Tickets Table
CREATE TABLE `tickets` (
  `ticket_id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `assigned_to_id` BIGINT DEFAULT NULL,
  `subject` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=OPEN, 1=IN_PROGRESS, 2=PENDING, 3=RESOLVED, 4=CLOSED',
  `priority` tinyint NOT NULL DEFAULT '1' COMMENT '0=LOW, 1=MEDIUM, 2=HIGH, 3=CRITICAL',
  `stage` tinyint DEFAULT NULL COMMENT '0=AWAITING_ACCEPTANCE, 1=WITH_STAFF, 2=WITH_TECHNICIAN, 3=AWAITING_PARTS, 4=WAREHOUSE_REQUESTED, 5=SUPPLIER_ORDERED, 6=RESOLVED, 7=CLOSED',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `closed_at` datetime DEFAULT NULL,
  `archived` bit(1) NOT NULL DEFAULT b'0',
  `archived_at` datetime DEFAULT NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `fk_tickets_customer` (`customer_id`),
  KEY `fk_tickets_assigned_to` (`assigned_to_id`),
  CONSTRAINT `fk_tickets_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_tickets_assigned_to` FOREIGN KEY (`assigned_to_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB;

-- Responses Table
CREATE TABLE `responses` (
  `response_id` BIGINT NOT NULL AUTO_INCREMENT,
  `ticket_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `message` text NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`response_id`),
  KEY `fk_responses_ticket` (`ticket_id`),
  KEY `fk_responses_user` (`user_id`),
  CONSTRAINT `fk_responses_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  CONSTRAINT `fk_responses_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB;

-- Repairs Table
CREATE TABLE `repairs` (
  `repair_id` BIGINT NOT NULL AUTO_INCREMENT,
  `ticket_id` BIGINT NOT NULL,
  `technician_id` BIGINT NOT NULL,
  `diagnosis` text,
  `repair_details` text,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=PENDING, 1=IN_PROGRESS, 2=WAITING_FOR_PARTS, 3=COMPLETED, 4=FAILED',
  `start_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completion_date` datetime DEFAULT NULL,
  PRIMARY KEY (`repair_id`),
  KEY `fk_repairs_ticket` (`ticket_id`),
  KEY `fk_repairs_technician` (`technician_id`),
  CONSTRAINT `fk_repairs_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  CONSTRAINT `fk_repairs_technician` FOREIGN KEY (`technician_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB;

-- PartRequests Table
CREATE TABLE `part_requests` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `part_id` BIGINT NOT NULL,
  `requestor_id` BIGINT NOT NULL,
  `repair_id` BIGINT DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `reason` text,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=PENDING, 1=APPROVED, 2=FULFILLED, 3=REJECTED, 4=PENDING_WAREHOUSE, 5=PURCHASE_ORDERED',
  `request_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fulfillment_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_partrequests_part` (`part_id`),
  KEY `fk_partrequests_requestor` (`requestor_id`),
  KEY `fk_partrequests_repair` (`repair_id`),
  CONSTRAINT `fk_partrequests_part` FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`),
  CONSTRAINT `fk_partrequests_requestor` FOREIGN KEY (`requestor_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_partrequests_repair` FOREIGN KEY (`repair_id`) REFERENCES `repairs` (`repair_id`)
) ENGINE=InnoDB;

-- PurchaseOrders Table
CREATE TABLE `purchase_orders` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_by_id` BIGINT NOT NULL,
  `supplier_id` BIGINT NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=PENDING, 1=APPROVED, 2=SHIPPED, 3=DELIVERED, 4=CANCELLED',
  `order_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expected_delivery` datetime DEFAULT NULL,
  `actual_delivery` datetime DEFAULT NULL,
  `vendor_name` varchar(100) NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `fk_purchaseorders_createdby` (`created_by_id`),
  KEY `fk_purchaseorders_supplier` (`supplier_id`),
  CONSTRAINT `fk_purchaseorders_createdby` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_purchaseorders_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`)
) ENGINE=InnoDB;

-- OrderItems Table
CREATE TABLE `order_items` (
  `order_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_id`,`part_id`),
  KEY `fk_orderitems_part` (`part_id`),
  CONSTRAINT `fk_orderitems_order` FOREIGN KEY (`order_id`) REFERENCES `purchase_orders` (`order_id`),
  CONSTRAINT `fk_orderitems_part` FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
) ENGINE=InnoDB;


-- ===================================================================
-- Sample Data Insertion (Using ORDINAL values for enums)
-- ===================================================================

-- Users: 0=CUSTOMER, 1=STAFF, 2=TECHNICIAN, 3=PRODUCT_MANAGER, 4=WAREHOUSE_MANAGER, 5=ADMIN
INSERT INTO `users` (username, password, email, full_name, phone_number, role) VALUES
('admin', 'root', 'admin@bytex.com', 'System Admin', '+94711234567', 5),
('staff', 'staff', 'staff@bytex.com', 'John Staff', '+94712345678', 1),
('tech', 'tech', 'tech@bytex.com', 'Mike Technician', '+94714567890', 2),
('pm', 'pm', 'pm@bytex.com', 'Patricia Manager', '+94716789012', 3),
('wm', 'wm', 'wm@bytex.com', 'Walter Manager', '+94717890123', 4),
('customer', 'customer', 'customer@gmail.com', 'Chris Customer', '+94718901234', 0);

-- Parts: 0=ACTIVE, 1=LOW_STOCK, 2=OUT_OF_STOCK, 3=DISCONTINUED
INSERT INTO `parts` (part_number, part_name, description, current_stock, minimum_stock, unit_price, category, status) VALUES
('CPU-001', 'Intel Core i9-13900K', 'High-performance CPU for gaming and productivity.', 10, 5, 589.99, 'CPU', 0),
('GPU-001', 'NVIDIA RTX 4090', 'Top-tier graphics card for 4K gaming.', 4, 3, 1599.99, 'GPU', 1),
('RAM-001', 'Corsair Vengeance 32GB DDR5', 'High-speed DDR5 RAM for modern systems.', 25, 10, 129.99, 'RAM', 0),
('SSD-001', 'Samsung 980 Pro 2TB', 'Blazing fast NVMe SSD for OS and games.', 1, 5, 169.99, 'Storage', 2);

-- Suppliers
INSERT INTO `suppliers` (supplier_name, contact_info) VALUES
('Global Tech Imports', 'sales@globaltech.com'),
('PC Parts Direct', 'contact@pcpartsdirect.net'),
('Silicon Valley Distribution', 'orders@svd.com');

-- Supplier_Parts (Linking suppliers to the parts they provide)
-- Global Tech Imports (ID 1) supplies CPUs (ID 1) and GPUs (ID 2)
INSERT INTO `supplier_parts` (supplier_id, part_id) VALUES (1, 1), (1, 2);
-- PC Parts Direct (ID 2) supplies RAM (ID 3) and SSDs (ID 4)
INSERT INTO `supplier_parts` (supplier_id, part_id) VALUES (2, 3), (2, 4);
-- Silicon Valley Distribution (ID 3) supplies everything
INSERT INTO `supplier_parts` (supplier_id, part_id) VALUES (3, 1), (3, 2), (3, 3), (3, 4);

-- A sample ticket from Chris Customer (ID 6)
-- Status: 0=OPEN, Priority: 2=HIGH, Stage: 0=AWAITING_ACCEPTANCE
INSERT INTO `tickets` (customer_id, subject, description, status, priority, stage, created_at, updated_at) VALUES
(6, 'My computer is making a loud noise', 'Ever since I installed the new graphics card, there is a loud whirring noise. It gets worse when I play games.', 0, 2, 0, NOW(), NOW());
