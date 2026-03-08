-- ----------------------------
-- 教练表
-- ----------------------------
drop table if exists coaches;
create table coaches (
  coach_id          bigint(20)      not null auto_increment    comment '教练ID',
  username          varchar(30)     not null                   comment '教练账号',
  password          varchar(100)    default ''                 comment '密码',
  nick_name         varchar(30)     not null                   comment '教练昵称',
  email             varchar(50)     default ''                 comment '邮箱',
  phone             varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '性别（0男 1女 2未知）',
  avatar            varchar(255)    default ''                 comment '头像URL地址',
  store_id          bigint(20)      not null                   comment '所属店铺ID',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (coach_id),
  key idx_store_id (store_id)
) engine=innodb auto_increment=1 comment = '教练表';

-- ----------------------------
-- 初始化-教练表数据
-- ----------------------------
insert into coaches (username, password, nick_name, email, phone, sex, avatar, store_id, status, login_ip, login_date, create_time, update_time) values
('chenyi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '陈一', 'chenyi@coach.com', '13700000001', '0', '/avatar/chenyi.jpg', 1, '0', '10.10.1.1', sysdate(), sysdate(), null),
('wanger', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王二', 'wanger@coach.com', '13700000002', '0', '/avatar/wanger.jpg', 1, '0', '10.10.1.2', sysdate(), sysdate(), null),
('zhangsan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三丰', 'zhangsf@coach.com', '13700000003', '0', '/avatar/zhangsanfeng.jpg', 2, '0', '10.10.1.3', sysdate(), sysdate(), null),
('lisi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四光', 'lisg@coach.com', '13700000004', '0', '/avatar/lisiguang.jpg', 3, '0', '10.10.1.4', sysdate(), sysdate(), null),
('wangwu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五环', 'wangwh@coach.com', '13700000005', '0', '/avatar/wangwuhuan.jpg', 4, '0', '10.10.1.5', sysdate(), sysdate(), null),
('zhaoliu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六六', 'zhaoll@coach.com', '13700000006', '1', '/avatar/zhaoliuliu.jpg', 5, '0', '10.10.1.6', sysdate(), sysdate(), null),
('sunqi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '孙七七', 'sunqi@coach.com', '13700000007', '1', '/avatar/sunqiqi.jpg', 6, '0', '10.10.1.7', sysdate(), sysdate(), null),
('zhouba', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '周八八', 'zhoubb@coach.com', '13700000008', '0', '/avatar/zhoubaba.jpg', 7, '1', '10.10.1.8', sysdate(), sysdate(), null),
('wujiu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '吴久久', 'wujiu@coach.com', '13700000009', '0', '/avatar/wujiu.jpg', 8, '0', '10.10.1.9', sysdate(), sysdate(), null),
('zhengshi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '郑拾拾', 'zhengss@coach.com', '13700000010', '1', '/avatar/zhengshishi.jpg', 9, '0', '10.10.1.10', sysdate(), sysdate(), null);