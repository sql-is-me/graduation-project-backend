DROP TABLE IF EXISTS operLog;
CREATE TABLE operLog (
  oper_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) DEFAULT NULL,
  business_type INT DEFAULT 0,
  method VARCHAR(255) DEFAULT NULL,
  request_method VARCHAR(10) DEFAULT NULL,
  operator_type INT DEFAULT 0,
  oper_name VARCHAR(64) DEFAULT NULL,
  oper_url VARCHAR(255) DEFAULT NULL,
  oper_ip VARCHAR(128) DEFAULT NULL,
  oper_param TEXT,
  json_result TEXT,
  status INT DEFAULT 0,
  error_msg VARCHAR(2000) DEFAULT NULL,
  oper_time DATETIME DEFAULT NULL,
  cost_time BIGINT DEFAULT 0,
  PRIMARY KEY (oper_id),
  KEY idx_oper_name (oper_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO operLog (oper_id, title, business_type, method, request_method, operator_type, oper_name, oper_url, oper_ip, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES
(1, '店铺管理', 1, 'StoreController.add', 'POST', 1, 'store_admin_1', '/admin/store/add', '10.0.0.2', '{"storeName":"阳光羽毛球馆"}', '{"code":200}', 0, NULL, '2026-03-18 20:00:00', 120),
(2, '优惠券管理', 2, 'CouponController.update', 'PUT', 1, 'store_admin_1', '/admin/coupon/update', '10.0.0.2', '{"couponId":1}', '{"code":200}', 0, NULL, '2026-03-18 20:05:00', 95),
(3, '课程查询', 4, 'CourseController.list', 'GET', 3, 'member_1', '/user/course/list', '192.168.1.1', '{}', '{"code":200}', 0, NULL, '2026-03-18 20:10:00', 40);
