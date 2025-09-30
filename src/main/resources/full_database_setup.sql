-- Drop tables in reverse order of creation to avoid foreign key constraints
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS supplier_parts;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS part_requests;
DROP TABLE IF EXISTS repair_parts;
DROP TABLE IF EXISTS repairs;
DROP TABLE IF EXISTS attachments;
DROP TABLE IF EXISTS responses;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS activity_logs;
DROP TABLE IF EXISTS users;

-- Create Users table
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'STAFF', 'TECHNICIAN', 'PRODUCT_MANAGER', 'WAREHOUSE_MANAGER', 'ADMIN')),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME NULL
);

-- Create Tickets table
CREATE TABLE tickets (
    ticket_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'PENDING', 'RESOLVED', 'CLOSED')),
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    category VARCHAR(50),
    assigned_to_id BIGINT NULL,
    stage VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    closed_at DATETIME NULL,
    archived BOOLEAN DEFAULT FALSE NOT NULL,
    archived_at DATETIME NULL,
    FOREIGN KEY (customer_id) REFERENCES users(user_id),
    FOREIGN KEY (assigned_to_id) REFERENCES users(user_id)
);

-- Create Responses table
CREATE TABLE responses (
    response_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create Attachments table
CREATE TABLE attachments (
    attachment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT,
    response_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size INT NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by BIGINT NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    FOREIGN KEY (response_id) REFERENCES responses(response_id),
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id),
    CHECK ((ticket_id IS NULL AND response_id IS NOT NULL) OR (ticket_id IS NOT NULL AND response_id IS NULL))
);

-- Create Repairs table
CREATE TABLE repairs (
    repair_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    diagnosis TEXT NOT NULL,
    repair_details TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'WAITING_FOR_PARTS', 'COMPLETED', 'FAILED')),
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    completion_date DATETIME NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
    FOREIGN KEY (technician_id) REFERENCES users(user_id)
);

-- Create Parts table
CREATE TABLE parts (
    part_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_number VARCHAR(50) NOT NULL UNIQUE,
    part_name VARCHAR(100) NOT NULL,
    description TEXT,
    current_stock INT NOT NULL DEFAULT 0,
    minimum_stock INT NOT NULL DEFAULT 5,
    unit_price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED'))
);

-- Create RepairParts junction table
CREATE TABLE repair_parts (
    repair_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (repair_id, part_id),
    FOREIGN KEY (repair_id) REFERENCES repairs(repair_id),
    FOREIGN KEY (part_id) REFERENCES parts(part_id)
);

-- Create PartRequests table
CREATE TABLE part_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_id BIGINT NOT NULL,
    requestor_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'FULFILLED', 'REJECTED')),
    request_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fulfillment_date DATETIME NULL,
    FOREIGN KEY (part_id) REFERENCES parts(part_id),
    FOREIGN KEY (requestor_id) REFERENCES users(user_id)
);

-- Create Suppliers table
CREATE TABLE suppliers (
    supplier_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT
);

-- Create SupplierParts junction table
CREATE TABLE supplier_parts (
    supplier_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    PRIMARY KEY (supplier_id, part_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (part_id) REFERENCES parts(part_id)
);

-- Create PurchaseOrders table
CREATE TABLE purchase_orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expected_delivery DATETIME NULL,
    actual_delivery DATETIME NULL,
    FOREIGN KEY (created_by_id) REFERENCES users(user_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Create OrderItems junction table
CREATE TABLE order_items (
    order_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (order_id, part_id),
    FOREIGN KEY (order_id) REFERENCES purchase_orders(order_id),
    FOREIGN KEY (part_id) REFERENCES parts(part_id)
);

-- Create ActivityLogs table
CREATE TABLE activity_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    description TEXT,
    ip_address VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert Sample Data
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

INSERT INTO tickets (customer_id, subject, description, status, priority, category, assigned_to_id) VALUES
(8, 'PC is overheating when playing games', 'My computer gets very hot and sometimes shuts down when I play modern games for more than 30 minutes. I have cleaned the fans.', 'OPEN', 'HIGH', 'Hardware', NULL),
(9, 'Windows fails to update', 'I keep getting error code 0x80070002 when trying to install the latest Windows 11 updates. I have tried the troubleshooter with no luck.', 'IN_PROGRESS', 'MEDIUM', 'Software', 2);