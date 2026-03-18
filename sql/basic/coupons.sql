DROP TABLE IF EXISTS coupons;
CREATE TABLE coupons (
  coupon_id BIGINT NOT NULL AUTO_INCREMENT,
  coupon_name VARCHAR(100) NOT NULL,
  store_id BIGINT NOT NULL,
  creator_id BIGINT NOT NULL,
  coupon_type CHAR(1) NOT NULL,
  discount_value DECIMAL(10,2) NOT NULL,
  min_amount DECIMAL(10,2) DEFAULT 0.00,
  total_count INT NOT NULL,
  remaining_count INT NOT NULL,
  claim_limit INT NOT NULL DEFAULT 1,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (coupon_id),
  KEY idx_coupon_store_id (store_id),
  KEY idx_coupon_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO coupons (coupon_id, coupon_name, store_id, creator_id, coupon_type, discount_value, min_amount, total_count, remaining_count, claim_limit, start_time, end_time, status, create_time, update_time) VALUES
(1, '满300减50', 1, 2, '0', 50.00, 300.00, 100, 80, 1, '2026-03-01 00:00:00', '2026-04-01 23:59:59', '0', '2026-03-18 13:00:00', '2026-03-18 13:00:00'),
(2, '满100减20', 1, 2, '0', 20.00, 100.00, 200, 150, 2, '2026-03-01 00:00:00', '2026-04-01 23:59:59', '0', '2026-03-18 13:05:00', '2026-03-18 13:05:00'),
(3, '九折优惠券', 2, 3, '1', 9.00, 0.00, 120, 100, 1, '2026-03-05 00:00:00', '2026-04-05 23:59:59', '0', '2026-03-18 13:10:00', '2026-03-18 13:10:00');
