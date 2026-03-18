DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  order_id BIGINT NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  product_type CHAR(1) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  discount_amount DECIMAL(10,2) DEFAULT 0.00,
  pay_amount DECIMAL(10,2) NOT NULL,
  coupon_id BIGINT DEFAULT NULL,
  status CHAR(1) NOT NULL,
  pay_type VARCHAR(20) DEFAULT NULL,
  pay_time DATETIME DEFAULT NULL,
  transaction_id VARCHAR(64) DEFAULT NULL,
  cancel_time DATETIME DEFAULT NULL,
  cancel_reason VARCHAR(255) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (order_id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_order_user_id (user_id),
  KEY idx_order_store_id (store_id),
  KEY idx_order_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO orders (order_id, order_no, user_id, store_id, product_type, quantity, unit_price, total_amount, discount_amount, pay_amount, coupon_id, status, pay_type, pay_time, transaction_id, cancel_time, cancel_reason, create_time, update_time) VALUES
(1, 'ORD202603180001', 1, 1, '0', 10, 100.00, 1000.00, 50.00, 950.00, 1, '1', 'wechat', '2026-03-18 16:00:00', 'WXTXN0001', NULL, NULL, '2026-03-18 15:55:00', '2026-03-18 16:00:00'),
(2, 'ORD202603180002', 2, 1, '0', 5, 100.00, 500.00, 0.00, 500.00, NULL, '0', NULL, NULL, NULL, NULL, NULL, '2026-03-18 16:05:00', '2026-03-18 16:05:00'),
(3, 'ORD202603180003', 1, 2, '0', 8, 120.00, 960.00, 0.00, 960.00, NULL, '2', NULL, NULL, NULL, '2026-03-18 16:30:00', '用户取消', '2026-03-18 16:10:00', '2026-03-18 16:30:00');
