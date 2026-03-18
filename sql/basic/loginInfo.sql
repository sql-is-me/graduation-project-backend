DROP TABLE IF EXISTS loginInfo;
CREATE TABLE loginInfo (
  info_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  status CHAR(1) NOT NULL,
  ip_addr VARCHAR(128) DEFAULT NULL,
  msg VARCHAR(255) DEFAULT NULL,
  access_time DATETIME DEFAULT NULL,
  PRIMARY KEY (info_id),
  KEY idx_login_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO loginInfo (info_id, username, status, ip_addr, msg, access_time) VALUES
(1, 'topadmin', '0', '10.0.0.1', '登录成功', '2026-03-18 19:00:00'),
(2, 'member_1', '0', '192.168.1.1', '登录成功', '2026-03-18 19:05:00'),
(3, 'store_admin_1', '1', '10.0.0.2', '密码错误', '2026-03-18 19:10:00');
