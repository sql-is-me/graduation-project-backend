-- ----------------------------
-- 管理员表
-- ----------------------------
drop table if exists admins;
create table admins (
  admin_id          bigint(20)      not null auto_increment    comment '管理员ID',
  username          varchar(30)     not null                   comment '管理员账号',
  password          varchar(100)    default ''                 comment '密码',
  nick_name         varchar(30)     not null                   comment '管理员昵称',
  email             varchar(50)     default ''                 comment '邮箱',
  phone             varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '性别（0男1女2未知）',
  avatar            varchar(255)    default ''                 comment '头像URL地址',
  store_id          bigint(20)      default null               comment '所属店铺ID（管理员类型为STORE时必填）',
  admin_type        char(1)         default '1'                comment '管理员类型（0:TOP 超级管理员/ 1:STORE 店铺管理员）',
  referrer_id       bigint(20)      default null               comment '推荐人ID（关联本表admin_id）',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (admin_id),
  unique key uk_admins_username (username),
  key idx_store_id (store_id),
  key idx_referrer_id (referrer_id)
) engine=innodb auto_increment=1 comment = '管理员表';

-- ----------------------------
-- 初始�?管理员表数据
-- ----------------------------
insert into admins (username, password, nick_name, email, phone, sex, avatar, store_id, admin_type, referrer_id, status, login_ip, login_date, create_time, update_time) values
('topadmin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'top@admin.com', '13900000001', '0', '/avatar/topadmin.jpg', null, '0', null, '0', '10.0.0.1', sysdate(), sysdate(), null),
('zhangwei', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张伟', 'zhangwei@store.com', '13900000002', '0', '/avatar/zhangwei.jpg', 1, '1', 1, '0', '10.0.0.2', sysdate(), sysdate(), null),
('lina', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李娜', 'lina@store.com', '13900000003', '1', '/avatar/lina.jpg', 1, '1', 2, '0', '10.0.0.3', sysdate(), sysdate(), null),
('wangqiang', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王强', 'wangqiang@store.com', '13900000004', '0', '/avatar/wangqiang.jpg', 1, '1', 3, '0', '10.0.0.4', sysdate(), sysdate(), null),
('zhaomin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵敏', 'zhaomin@store.com', '13900000005', '1', '/avatar/zhaomin.jpg', 2, '1', 1, '0', '10.0.0.5', sysdate(), sysdate(), null),
('chenlong', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '陈龙', 'chenlong@store.com', '13900000006', '0', '/avatar/chenlong.jpg', 3, '1', 1, '0', '10.0.0.6', sysdate(), sysdate(), null),
('liuyan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '刘燕', 'liuyan@store.com', '13900000007', '1', '/avatar/liuyan.jpg', 4, '1', 1, '0', '10.0.0.7', sysdate(), sysdate(), null),
('zhoujie', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '周杰', 'zhoujie@store.com', '13900000008', '0', '/avatar/zhoujie.jpg', 5, '1', 1, '1', '10.0.0.8', sysdate(), sysdate(), null),
('wudi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '吴迪', 'wudi@store.com', '13900000009', '0', '/avatar/wudi.jpg', 6, '1', 1, '0', '10.0.0.9', sysdate(), sysdate(), null),
('linxin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '林欣', 'linxin@store.com', '13900000010', '1', '/avatar/linxin.jpg', 7, '1', 1, '0', '10.0.0.10', sysdate(), sysdate(), null);
