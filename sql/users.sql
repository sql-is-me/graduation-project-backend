-- ----------------------------
-- 用户表
-- ----------------------------
drop table if exists users;
create table users (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  username          varchar(30)     not null                   comment '用户账号',
  password          varchar(100)    default ''                 comment '密码',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phone             varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(255)    default ''                 comment '头像URL地址',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  create_time       datetime                                   comment '注册时间',
  update_time       datetime                                   comment '更新时间',
  primary key (user_id)
) engine=innodb auto_increment=1 comment = '用户表';

-- ----------------------------
-- 初始化-用户表数据
-- ----------------------------
insert into users (username, password, nick_name, email, phone, sex, child_count, avatar, status, login_ip, login_date, create_time, update_time) values
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', 'admin@163.com', '13800000001', '0', '/avatar/admin.jpg', '0', '127.0.0.1', sysdate(), sysdate(), null),
('zhangsan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', 'zhangsan@qq.com', '13800000002', '0', '/avatar/zhangsan.png', '0', '192.168.1.1', sysdate(), sysdate(), null),
('lisi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', 'lisi@qq.com', '13800000003', '0', '/avatar/lisi.jpg', '0', '192.168.1.2', sysdate(), sysdate(), null),
('wangwu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', 'wangwu@qq.com', '13800000004', '1', '/avatar/wangwu.jpg', '0', '192.168.1.3', sysdate(), sysdate(), null),
('zhaoliu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', 'zhaoliu@qq.com', '13800000005', '1', '/avatar/zhaoliu.png', '0', '192.168.1.4', sysdate(), sysdate(), null),
('sunqi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '孙七', 'sunqi@qq.com', '13800000006', '0', '', '0', '192.168.1.5', sysdate(), sysdate(), null),
('zhouba', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '周八', 'zhouba@qq.com', '13800000007', '0', '/avatar/zhouba.jpg', '0', '192.168.1.6', sysdate(), sysdate(), null),
('wujiu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '吴九', 'wujiu@qq.com', '13800000008', '1', '/avatar/wujiu.jpg', '1', '192.168.1.7', sysdate(), sysdate(), null),
('zhengshi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '郑十', 'zhengshi@qq.com', '13800000009', '2', '', '0', '192.168.1.8', sysdate(), sysdate(), null),
('test', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '测试员', 'test@qq.com', '13800000010', '0', '/avatar/default.png', '0', '192.168.1.9', sysdate(), sysdate(), null);