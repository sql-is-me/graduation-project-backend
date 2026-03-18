DROP TABLE IF EXISTS notices;
CREATE TABLE notices (
  notice_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content TEXT,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_by VARCHAR(64) DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO notices (notice_id, title, content, status, create_by, create_time, update_by, update_time) VALUES
(1, '节假日休馆通知', '法定节假日期间本馆暂停营业，请各位会员知悉。', '0', 'topadmin', '2026-03-18 18:00:00', 'topadmin', '2026-03-18 18:00:00'),
(2, '春季优惠券上线', '春季优惠活动已开启，快来领取优惠券吧！', '0', 'store_admin_1', '2026-03-18 18:05:00', 'store_admin_1', '2026-03-18 18:05:00'),
(3, '系统维护公告', '本周末将进行系统升级维护，届时暂停服务。', '1', 'topadmin', '2026-03-18 18:10:00', 'topadmin', '2026-03-18 18:10:00');
