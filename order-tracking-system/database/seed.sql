-- Order Tracking System - sample data (timestamps and locations are illustrative)
-- Run this AFTER schema.sql

USE order_tracker;

INSERT INTO orders (order_id, customer_name, product_name, current_status, created_at) VALUES
('ORD-1001', 'Aarav Sharma',   'Dell Inspiron 15 Laptop',     'PLACED',           '2026-10-19 09:15:00'),
('ORD-1002', 'Priya Nair',     'Noise-Cancelling Headphones', 'PACKED',           '2026-10-18 11:30:00'),
('ORD-1003', 'Rohan Mehta',    'Mechanical Keyboard',         'SHIPPED',          '2026-10-16 10:05:00'),
('ORD-1004', 'Sneha Kulkarni', 'Ergonomic Office Chair',      'OUT_FOR_DELIVERY', '2026-10-13 14:10:00'),
('ORD-1005', 'Imran Shaikh',   '27-inch 4K Monitor',          'DELIVERED',        '2026-10-09 16:40:00');

INSERT INTO tracking_history (order_id, status, location, remarks, event_time) VALUES
-- ORD-1001: just placed (1 event)
('ORD-1001', 'PLACED',           'Online Store, Mumbai',                    'Order placed by customer',               '2026-10-19 09:15:00'),

-- ORD-1002: packed (2 events)
('ORD-1002', 'PLACED',           'Online Store, Mumbai',                    'Order placed by customer',               '2026-10-18 11:30:00'),
('ORD-1002', 'PACKED',           'Warehouse, Bhiwandi',                     'Order confirmed and packed',             '2026-10-18 16:45:00'),

-- ORD-1003: shipped / in transit (3 events)
('ORD-1003', 'PLACED',           'Online Store, Mumbai',                    'Order placed by customer',               '2026-10-16 10:05:00'),
('ORD-1003', 'PACKED',           'Warehouse, Bhiwandi',                     'Order confirmed and packed',             '2026-10-16 17:20:00'),
('ORD-1003', 'SHIPPED',          'Dispatch Center, Bhiwandi, Maharashtra',  'Handed over to delivery partner',        '2026-10-17 08:40:00'),

-- ORD-1004: out for delivery (4 events)
('ORD-1004', 'PLACED',           'Online Store, Mumbai',                    'Order placed by customer',               '2026-10-13 14:10:00'),
('ORD-1004', 'PACKED',           'Warehouse, Pune',                         'Order confirmed and packed',             '2026-10-14 10:30:00'),
('ORD-1004', 'SHIPPED',          'Dispatch Center, Pune, Maharashtra',      'Handed over to delivery partner',        '2026-10-14 19:00:00'),
('ORD-1004', 'OUT_FOR_DELIVERY', 'Local Delivery Hub, Andheri, Mumbai',     'Out for delivery, expected today',       '2026-10-16 08:50:00'),

-- ORD-1005: delivered, the full 5-event journey
('ORD-1005', 'PLACED',           'Online Store, Mumbai',                    'Order placed by customer',               '2026-10-09 16:40:00'),
('ORD-1005', 'PACKED',           'Warehouse, Bhiwandi',                     'Order confirmed and packed',             '2026-10-10 11:15:00'),
('ORD-1005', 'SHIPPED',          'Dispatch Center, Bhiwandi, Maharashtra',  'Handed over to delivery partner',        '2026-10-10 18:30:00'),
('ORD-1005', 'OUT_FOR_DELIVERY', 'Local Delivery Hub, Andheri, Mumbai',     'Out for delivery',                       '2026-10-12 08:20:00'),
('ORD-1005', 'DELIVERED',        'Customer Address, Andheri West, Mumbai',  'Delivered to customer, signature received', '2026-10-12 13:05:00');
