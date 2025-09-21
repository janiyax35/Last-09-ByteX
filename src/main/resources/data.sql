-- Clear existing data to avoid conflicts on restart
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE users;
-- TRUNCATE TABLE parts;
-- TRUNCATE TABLE suppliers;
-- TRUNCATE TABLE tickets;
-- TRUNCATE TABLE responses;
-- TRUNCATE TABLE repairs;
-- TRUNCATE TABLE part_requests;
-- TRUNCATE TABLE purchase_orders;
-- TRUNCATE TABLE order_items;
-- SET FOREIGN_KEY_CHECKS = 1;

-- Note: IDs are auto-generated.
-- Roles must be uppercase as defined in the Enum: ADMIN, STAFF, TECHNICIAN, PRODUCT_MANAGER, WAREHOUSE_MANAGER, CUSTOMER

-- Admin user (ID: 1)
INSERT INTO users (username, password, email, full_name, phone_number, role, created_at, last_login) VALUES
('admin', 'admin123', 'admin@bytex.com', 'System Administrator', '+94711234567', 'ADMIN', '2025-07-01 08:30:00', '2025-08-25 14:00:00');

-- Staff members (IDs: 2, 3)
INSERT INTO users (username, password, email, full_name, phone_number, role, created_at, last_login) VALUES
('john.staff', 'staff123', 'john@bytex.com', 'John Smith', '+94712345678', 'STAFF', '2025-07-05 09:15:00', '2025-08-24 16:45:00'),
('sarah.staff', 'staff123', 'sarah@bytex.com', 'Sarah Johnson', '+94713456789', 'STAFF', '2025-07-06 10:30:00', '2025-08-25 09:20:00');

-- Technicians (IDs: 4, 5)
INSERT INTO users (username, password, email, full_name, phone_number, role, created_at, last_login) VALUES
('mike.tech', 'tech123', 'mike@bytex.com', 'Mike Chen', '+94714567890', 'TECHNICIAN', '2025-07-10 08:00:00', '2025-08-25 12:30:00'),
('laura.tech', 'tech123', 'laura@bytex.com', 'Laura Silva', '+94715678901', 'TECHNICIAN', '2025-07-11 08:30:00', '2025-08-24 17:15:00');

-- Product Manager (ID: 6)
INSERT INTO users (username, password, email, full_name, phone_number, role, created_at, last_login) VALUES
('david.pm', 'pm123', 'david@bytex.com', 'David Perera', '+94716789012', 'PRODUCT_MANAGER', '2025-07-15 09:45:00', '2025-08-25 10:30:00');

-- Warehouse Manager (ID: 7)
INSERT INTO users (username, password, email, full_name, phone_number, role, created_at, last_login) VALUES
('priya.wm', 'wm123', 'priya@bytex.com', 'Priya Fernando', '+94717890123', 'WAREHOUSE_MANAGER', '2025-07-20 08:15:00', '2025-08-24 15:45:00');

-- Customers (IDs: 8, 9, 10, 11, 12)
INSERT INTO users (username, password, email, full_name, phone_number, role, created_at, last_login) VALUES
('raj.customer', 'pass123', 'raj@gmail.com', 'Raj Mendis', '+94718901234', 'CUSTOMER', '2025-07-25 14:20:00', '2025-08-23 18:30:00'),
('anita.customer', 'pass123', 'anita@outlook.com', 'Anita De Silva', '+94719012345', 'CUSTOMER', '2025-07-26 15:45:00', '2025-08-22 19:15:00'),
('kumar.customer', 'pass123', 'kumar@yahoo.com', 'Kumar Bandara', '+94720123456', 'CUSTOMER', '2025-07-27 16:30:00', '2025-08-24 09:45:00'),
('michelle.customer', 'pass123', 'michelle@gmail.com', 'Michelle Gunasekera', '+94721234567', 'CUSTOMER', '2025-07-28 10:15:00', '2025-08-25 11:20:00'),
('saman.customer', 'pass123', 'saman@outlook.com', 'Saman Jayawardena', '+94722345678', 'CUSTOMER', '2025-07-29 11:45:00', '2025-08-21 14:30:00');

-- Sample parts (IDs: 1, 2, 3, 4)
INSERT INTO parts (part_number, part_name, description, current_stock, minimum_stock, unit_price, category, status) VALUES
('CPU001', 'Intel Core i7-12700K', 'High performance CPU', 15, 5, 399.99, 'CPU', 'ACTIVE'),
('RAM001', 'Corsair Vengeance 16GB DDR4', 'High performance RAM', 30, 10, 79.99, 'RAM', 'ACTIVE'),
('GPU001', 'NVIDIA RTX 3080', 'High-end graphics card', 5, 3, 699.99, 'GPU', 'LOW_STOCK'),
('SSD001', 'Samsung 970 EVO 1TB', 'NVMe SSD', 0, 5, 149.99, 'Storage', 'OUT_OF_STOCK');

-- Sample suppliers (ID: 1)
INSERT INTO suppliers(supplier_name, contact_info, address) VALUES
('Global PC Parts Inc.', 'sales@globalpc.com', '123 Tech Road, Silicon Valley, USA');

-- ===================================================================
-- S A M P L E    W O R K F L O W    D A T A
-- ===================================================================

-- Ticket 1: Simple ticket, assigned to staff (ID: 1)
INSERT INTO tickets (customer_id, subject, description, status, priority, assigned_to_id) VALUES
(8, 'My computer is running slow', 'Ever since the last update, my PC takes forever to boot up.', 'IN_PROGRESS', 'MEDIUM', 2);

-- Response for Ticket 1 from staff member John (ID: 1)
INSERT INTO responses (ticket_id, user_id, message) VALUES
(1, 2, 'Hi Raj, thanks for reaching out. I am looking into this issue for you.');

-- Ticket 2: Escalated to technician, requires parts (ID: 2)
INSERT INTO tickets (customer_id, subject, description, status, priority, assigned_to_id) VALUES
(9, 'Graphics card not detected', 'My new monitor is blank, and the system does not see my graphics card.', 'IN_PROGRESS', 'HIGH', 3);

-- Repair record for Ticket 2, assigned to technician Mike (ID: 1)
INSERT INTO repairs (ticket_id, technician_id, diagnosis, status) VALUES
(2, 4, 'Initial diagnosis suggests the GPU may be faulty. Physical inspection required.', 'PENDING');

-- Part Request from Mike for Ticket 2's repair (ID: 1)
INSERT INTO part_requests (part_id, requestor_id, quantity, reason, status) VALUES
(3, 4, 1, 'Replacement for potentially faulty GPU in ticket #2.', 'PENDING');

-- ===================================================================
-- S A M P L E    A C T I V I T Y    L O G S
-- ===================================================================
INSERT INTO activity_logs (user_id, action_type, entity_type, entity_id, description, ip_address, created_at) VALUES
(1, 'LOGIN', 'USER', 1, 'Admin user logged in successfully.', '127.0.0.1', '2025-08-25 14:00:00'),
(8, 'CREATE', 'TICKET', 1, 'Customer raj.customer created a new ticket ''My computer is running slow''', '192.168.1.10', '2025-08-26 10:00:00'),
(2, 'UPDATE', 'TICKET', 1, 'Staff john.staff updated ticket status to IN_PROGRESS.', '192.168.1.20', '2025-08-26 10:05:00'),
(9, 'CREATE', 'TICKET', 2, 'Customer anita.customer created a new ticket ''Graphics card not detected''', '192.168.1.12', '2025-08-27 11:00:00');
