-- ----------------------------
-- 孩子表
-- ----------------------------
drop table if exists children;
create table children (
  child_id          bigint(20)      not null auto_increment    comment '孩子ID',
  parent_id         bigint(20)      not null                   comment '父母ID（关联用户表user_id）',
  child_name        varchar(30)     not null                   comment '孩子姓名',
  birthday          date                                       comment '出生日期',
  photo             varchar(255)    default ''                 comment '孩子照片URL地址',
  sex               char(1)         default '0'                comment '孩子性别（0男孩 1女孩 2未知）',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (child_id),
  key idx_parent_id (parent_id)
) engine=innodb auto_increment=1 comment = '孩子表';

-- ----------------------------
-- 初始化-孩子表数据
-- ----------------------------
insert into children (parent_id, child_name, birthday, photo, sex, create_time, update_time) values
(1, '小管理员', '2020-01-15', '/children/child1.jpg', '0', sysdate(), null),
(2, '张小宝', '2019-05-20', '/children/child2.jpg', '1', sysdate(), null),
(2, '张小贝', '2021-08-10', '/children/child3.jpg', '0', sysdate(), null),
(3, '李小乐', '2018-11-03', '/children/child4.jpg', '0', sysdate(), null),
(4, '王小萌', '2022-02-28', '/children/child5.jpg', '1', sysdate(), null),
(4, '王小帅', '2020-07-12', '/children/child6.jpg', '0', sysdate(), null),
(4, '王小美', '2023-04-18', '/children/child7.jpg', '1', sysdate(), null),
(5, '赵小朵', '2019-09-22', '/children/child8.jpg', '1', sysdate(), null),
(5, '赵小朵', '2021-12-05', '/children/child9.jpg', '1', sysdate(), null),
(7, '周小周', '2022-06-30', '/children/child10.jpg', '0', sysdate(), null),
(8, '吴小九', '2020-03-17', '/children/child11.jpg', '1', sysdate(), null),
(8, '吴小十', '2023-01-08', '/children/child12.jpg', '0', sysdate(), null),
(10, '测试小宝', '2021-10-01', '/children/child13.jpg', '0', sysdate(), null);
