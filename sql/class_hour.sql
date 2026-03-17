-- ----------------------------
-- 会员课时表
-- ----------------------------
drop table if exists class_hour;
create table class_hour (
  ch_id             bigint(20)      not null auto_increment    comment '课时记录ID',
  user_id           bigint(20)      not null                   comment '会员ID（关联用户表user_id）',
  used_hours      int(5)          default 0                  comment '已用课时数',
  remaining_hours int(5)          default 0                  comment '剩余课时数',
  update_time       datetime                                   comment '更新时间',
  primary key (ch_id),
  key idx_user_id (user_id)
) engine=innodb auto_increment=1 comment = '会员课时表';

-- ----------------------------
-- 初始化-会员课时表数据
-- ----------------------------
insert into class_hour (user_id, used_hours, remaining_hours, update_time) values
(1, 23, 77, sysdate()),
(2, 17, 63, sysdate()),
(3, 18, 2, sysdate()),
(4, 50, 70, sysdate()),
(5, 35, 25, sysdate()),
(6, 25, 0, sysdate()),
(7, 32, 48, sysdate()),
(8, 15, 0, sysdate()),
(9, 8, 42, sysdate()),
(10, 15, 35, sysdate());