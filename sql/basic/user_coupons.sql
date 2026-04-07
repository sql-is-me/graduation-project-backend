DROP TABLE IF EXISTS user_coupons;
CREATE TABLE user_coupons (
  user_coupon_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  coupon_id BIGINT NOT NULL,
  status CHAR(1) NOT NULL DEFAULT '0',
  used_order_id BIGINT DEFAULT NULL,
  claim_time DATETIME DEFAULT NULL,
  used_time DATETIME DEFAULT NULL,
  PRIMARY KEY (user_coupon_id),
  UNIQUE KEY uk_user_coupon (user_id, coupon_id),
  KEY idx_uc_user_id (user_id),
  KEY idx_uc_coupon_id (coupon_id),
  KEY idx_uc_used_order_id (used_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO user_coupons (user_coupon_id, user_id, coupon_id, status, used_order_id, claim_time, used_time) VALUES
(1, 1, 1, '1', 1, '2026-03-18 14:00:00', '2026-03-18 16:00:00'),
(2, 2, 2, '0', NULL, '2026-03-18 14:05:00', NULL),
(3, 1, 3, '2', NULL, '2026-03-18 14:10:00', NULL);
