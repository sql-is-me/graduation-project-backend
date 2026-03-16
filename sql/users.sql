-- ----------------------------
-- 用户表（会员+教练合并）
-- ----------------------------
drop table if exists users;
create table users (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  username          varchar(30)     not null                   comment '用户账号',
  password          varchar(100)    default ''                 comment '密码',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  user_type         char(1)         default '0'                comment '用户类型（0会员 1教练）',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phone             varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(255)    default ''                 comment '头像URL地址',
  store_id          bigint(20)      default null               comment '所属店铺ID',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  create_time       datetime                                   comment '注册时间',
  update_time       datetime                                   comment '更新时间',
  primary key (user_id),
  key idx_store_id (store_id)
) engine=innodb auto_increment=1 comment = '用户表（会员+教练）';

-- ----------------------------
-- 初始化-用户表数据（6条会员 + 4条教练）
-- ----------------------------
insert into users (username, password, nick_name, user_type, email, phone, sex, avatar, store_id, status, login_ip, login_date, create_time, update_time) values
('zhangsan',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三',   '0', 'zhangsan@qq.com',    '13800000001', '0', '', null, '0', '192.168.1.1',  sysdate(), sysdate(), null),
('lisi',      '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四',   '0', 'lisi@qq.com',        '13800000002', '0', '', null, '0', '192.168.1.2',  sysdate(), sysdate(), null),
('wangwu',    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五',   '0', 'wangwu@qq.com',      '13800000003', '1', '', null, '0', '192.168.1.3',  sysdate(), sysdate(), null),
('zhaoliu',   '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六',   '0', 'zhaoliu@qq.com',     '13800000004', '1', '', null, '0', '192.168.1.4',  sysdate(), sysdate(), null),
('sunqi',     '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '孙七',   '0', 'sunqi@qq.com',       '13800000005', '0', '', null, '0', '192.168.1.5',  sysdate(), sysdate(), null),
('zhouba',    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '周八',   '0', 'zhouba@qq.com',      '13800000006', '0', '', null, '0', '192.168.1.6',  sysdate(), sysdate(), null),
('coach_chen','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '陈教练', '1', 'chenyi@coach.com',   '13700000001', '0', '', 1,    '0', '10.10.1.1',    sysdate(), sysdate(), null),
('coach_wang','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王教练', '1', 'wanger@coach.com',   '13700000002', '0', '', 1,    '0', '10.10.1.2',    sysdate(), sysdate(), null),
('coach_li',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李教练', '1', 'lisg@coach.com',     '13700000003', '0', '', 2,    '0', '10.10.1.3',    sysdate(), sysdate(), null),
('coach_zhao','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵教练', '1', 'zhaoll@coach.com',   '13700000004', '1', '', 3,    '0', '10.10.1.4',    sysdate(), sysdate(), null);
