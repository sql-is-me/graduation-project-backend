SET NAMES utf8mb4;

-- ----------------------------
-- 1、店铺表
-- ----------------------------
drop table if exists stores;
create table stores (
  store_id          bigint(20)      not null auto_increment    comment '店铺id',
  store_name        varchar(50)     not null                   comment '店铺名称',
  leader            varchar(20)     default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  status            char(1)         default '0'                comment '店铺状态（0正常 1停用）',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (store_id)
) engine=innodb auto_increment=1 comment = '店铺表';

-- ----------------------------
-- 初始化-店铺表数据
-- ----------------------------
insert into stores (store_name, leader, phone, status, create_time, update_time) values
('王府井店', '张伟', '13800010001', '0', sysdate(), null),
('六里桥店', '李娜', '13800010002', '0', sysdate(), null),
('西单店', '王强', '13800010003', '0', sysdate(), null),
('东直门店', '赵敏', '13800010004', '0', sysdate(), null),
('朝阳门店', '刘洋', '13800010005', '1', sysdate(), null),
('中关村店', '陈晨', '13800010006', '0', sysdate(), null),
('望京店', '周杰', '13800010007', '0', sysdate(), null),
('亦庄店', '吴迪', '13800010008', '0', sysdate(), null),
('通州店', '郑爽', '13800010009', '1', sysdate(), null),
('大兴店', '林欣', '13800010010', '0', sysdate(), null);