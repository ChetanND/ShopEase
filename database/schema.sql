-- ShopEase MySQL schema and sample data
-- Run as a MySQL user with CREATE DATABASE privileges.

CREATE DATABASE IF NOT EXISTS shopease_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE shopease_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS contact_messages;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(120) NOT NULL,
    email           VARCHAR(160) NOT NULL UNIQUE,
    phone           VARCHAR(20)  NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    role            ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    status          ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE addresses (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    address_line    VARCHAR(255) NOT NULL,
    city            VARCHAR(80) NOT NULL,
    state           VARCHAR(80) NOT NULL,
    pincode         VARCHAR(10) NOT NULL,
    is_default      TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE categories (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(80) NOT NULL UNIQUE,
    description     VARCHAR(255),
    image_url       VARCHAR(500),
    status          ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

CREATE TABLE products (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(160) NOT NULL,
    description     TEXT NOT NULL,
    category_id     INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    discount        DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    stock           INT NOT NULL DEFAULT 0,
    image_url       VARCHAR(500) NOT NULL,
    rating          DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    status          ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_product_price CHECK (price >= 0),
    CONSTRAINT chk_product_discount CHECK (discount >= 0 AND discount <= 100),
    CONSTRAINT chk_product_stock CHECK (stock >= 0),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB;

CREATE TABLE cart (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL UNIQUE,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE cart_items (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cart_id         INT NOT NULL,
    product_id      INT NOT NULL,
    quantity        INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_cart_qty CHECK (quantity > 0),
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE orders (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    order_number    VARCHAR(32) NOT NULL UNIQUE,
    user_id         INT NOT NULL,
    full_name       VARCHAR(120) NOT NULL,
    email           VARCHAR(160) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    address_line    VARCHAR(255) NOT NULL,
    city            VARCHAR(80) NOT NULL,
    state           VARCHAR(80) NOT NULL,
    pincode         VARCHAR(10) NOT NULL,
    payment_method  ENUM('COD', 'ONLINE') NOT NULL,
    payment_status  ENUM('PENDING', 'PAID') NOT NULL DEFAULT 'PENDING',
    status          ENUM('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PLACED',
    subtotal        DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    shipping        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    grand_total     DECIMAL(10,2) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE order_items (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    order_id        INT NOT NULL,
    product_id      INT NOT NULL,
    product_name    VARCHAR(160) NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    quantity        INT NOT NULL,
    discount        DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    line_total      DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE reviews (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    product_id      INT NOT NULL,
    rating          INT NOT NULL,
    comment         VARCHAR(1000) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_user_product_review UNIQUE (user_id, product_id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE contact_messages (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    email           VARCHAR(160) NOT NULL,
    subject         VARCHAR(160) NOT NULL,
    message         VARCHAR(2000) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

-- Password format: salt:sha256(salt + password)
-- Admin@123 / Customer@123 / Customer2@123
INSERT INTO users (full_name, email, phone, password_hash, role, status) VALUES
('ShopEase Admin', 'admin@shopease.com', '9876543210',
 '7f3a9c2e1b84d6f0:7e8e58632d29c5ba482de74c44fec18634ecafc857e9a29fd4cfcd52f87d6a56',
 'ADMIN', 'ACTIVE'),
('Riya Sharma', 'customer@shopease.com', '9123456780',
 'c8e41a90b7d35f26:41e332cfd52af87545e1a4014c330828cb886246188aecbec9ae13adece6300e',
 'CUSTOMER', 'ACTIVE'),
('Amit Patel', 'amit@shopease.com', '9988776655',
 '11aa22bb33cc44dd:467a7b4f8dd5d2fd4bab00240dc8aac08c376e338e64a7348f9edf0b6f40bfb4',
 'CUSTOMER', 'ACTIVE');

INSERT INTO addresses (user_id, address_line, city, state, pincode, is_default) VALUES
(2, '14 MG Road, Koramangala', 'Bengaluru', 'Karnataka', '560034', 1),
(3, '88 SG Highway, Satellite', 'Ahmedabad', 'Gujarat', '380015', 1);

INSERT INTO categories (name, description, image_url, status) VALUES
('Electronics', 'Laptops, phones, and gadgets', 'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80', 'ACTIVE'),
('Clothing', 'Everyday wear and seasonal fashion', 'https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=800&q=80', 'ACTIVE'),
('Shoes', 'Sports and casual footwear', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80', 'ACTIVE'),
('Books', 'Fiction, learning, and lifestyle titles', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=80', 'ACTIVE'),
('Home & Kitchen', 'Appliances and home essentials', 'https://images.unsplash.com/photo-1556910096-6f5e72db6803?auto=format&fit=crop&w=800&q=80', 'ACTIVE'),
('Accessories', 'Bags, watches, and add-ons', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80', 'ACTIVE'),
('Beauty', 'Skincare and personal care', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80', 'ACTIVE');

INSERT INTO products (name, description, category_id, price, discount, stock, image_url, rating, status) VALUES
('AeroBook 15 Laptop', '15.6-inch Full HD laptop with 16GB RAM, 512GB SSD, and all-day battery for work and study.', 1, 62999.00, 12.00, 18,
 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=80', 4.50, 'ACTIVE'),
('NexPhone 12 Smartphone', '6.5-inch AMOLED display, 128GB storage, dual camera, and fast charging.', 1, 24999.00, 10.00, 32,
 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80', 4.30, 'ACTIVE'),
('Pulse Wireless Headphones', 'Over-ear Bluetooth headphones with 30-hour battery and deep bass.', 1, 3999.00, 15.00, 40,
 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80', 4.40, 'ACTIVE'),
('FitPulse Smart Watch', 'Fitness tracking, heart-rate monitor, and 7-day battery life.', 1, 5499.00, 8.00, 25,
 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80', 4.10, 'ACTIVE'),
('Cotton Crew T-Shirt', 'Soft mid-weight cotton tee with a regular fit. Machine washable.', 2, 799.00, 20.00, 80,
 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 4.20, 'ACTIVE'),
('Classic Slim Jeans', 'Stretch denim jeans with a modern slim cut and five-pocket styling.', 2, 1899.00, 18.00, 45,
 'https://images.unsplash.com/photo-1542272454315-4c4d0d0f43e7?auto=format&fit=crop&w=900&q=80', 4.00, 'ACTIVE'),
('All-Weather Jacket', 'Lightweight water-resistant jacket suitable for travel and daily commute.', 2, 3299.00, 22.00, 20,
 'https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504?auto=format&fit=crop&w=900&q=80', 4.35, 'ACTIVE'),
('TrailRun Running Shoes', 'Cushioned running shoes with breathable mesh and durable outsole.', 3, 2799.00, 16.00, 36,
 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80', 4.45, 'ACTIVE'),
('Urban Casual Shoes', 'Everyday sneakers with a memory-foam insole and clean silhouette.', 3, 2199.00, 10.00, 28,
 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=900&q=80', 4.15, 'ACTIVE'),
('The Art of Focus', 'Practical guide to building concentration and productive habits.', 4, 499.00, 5.00, 60,
 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80', 4.60, 'ACTIVE'),
('Kitchen Stories Cookbook', '80 approachable recipes for weeknight cooking and weekend baking.', 4, 899.00, 12.00, 22,
 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=900&q=80', 4.25, 'ACTIVE'),
('BrewLite Coffee Maker', 'Drip coffee maker with programmable timer and reusable filter.', 5, 3499.00, 14.00, 15,
 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&w=900&q=80', 4.05, 'ACTIVE'),
('Glow Table Lamp', 'Warm LED table lamp with three brightness levels for study or bedside use.', 5, 1299.00, 10.00, 30,
 'https://images.unsplash.com/photo-1507473883500-6e91862d3e2f?auto=format&fit=crop&w=900&q=80', 4.20, 'ACTIVE'),
('Travel Weekender Bag', 'Water-resistant duffel with shoe compartment and padded shoulder strap.', 6, 2499.00, 18.00, 16,
 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80', 4.30, 'ACTIVE'),
('Minimal Leather Wallet', 'Slim bifold wallet with RFID lining and six card slots.', 6, 999.00, 15.00, 50,
 'https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=900&q=80', 4.10, 'ACTIVE'),
('HydraGlow Face Cream', 'Lightweight moisturizer for daily hydration with a non-greasy finish.', 7, 649.00, 8.00, 42,
 'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80', 4.40, 'ACTIVE'),
('Sunrise Vitamin C Serum', 'Brightening serum with vitamin C and hyaluronic acid.', 7, 899.00, 12.00, 35,
 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=900&q=80', 4.55, 'ACTIVE'),
('Studio Bluetooth Speaker', 'Portable speaker with 12-hour playtime and splash-resistant body.', 1, 2299.00, 20.00, 24,
 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80', 4.25, 'ACTIVE'),
('Linen Shirt', 'Breathable linen-blend shirt for warm weather and smart-casual looks.', 2, 1599.00, 10.00, 27,
 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=900&q=80', 4.05, 'ACTIVE'),
('Ceramic Dinner Set', '16-piece stoneware dinner set in a matte finish. Microwave safe.', 5, 2799.00, 17.00, 12,
 'https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c?auto=format&fit=crop&w=900&q=80', 4.00, 'ACTIVE');

INSERT INTO reviews (user_id, product_id, rating, comment) VALUES
(2, 3, 5, 'Comfortable for long flights and the battery really lasts.'),
(3, 1, 4, 'Great value laptop for office work. Runs cool and the keyboard is solid.');
