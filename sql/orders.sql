-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID(关联users表)',
    `store_id` BIGINT DEFAULT NULL COMMENT '所属店铺ID(关联stores表)',
    `product_type` VARCHAR(10) NOT NULL DEFAULT '0' COMMENT '商品类型 0-课时购买',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量(课时数)',
    `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 1.00 COMMENT '单价(元)',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额(元)',
    `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额(元)',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额(元)',
    `coupon_id` BIGINT DEFAULT NULL COMMENT '使用的优惠券ID(关联user_coupons表)',
    `status` VARCHAR(2) NOT NULL DEFAULT '0' COMMENT '订单状态 0-待支付 1-已支付 2-已取消 3-已退款',
    `pay_type` VARCHAR(10) DEFAULT NULL COMMENT '支付方式 wechat-微信支付',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `transaction_id` VARCHAR(128) DEFAULT NULL COMMENT '微信支付交易号',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 优惠券模板表(管理员创建)
CREATE TABLE IF NOT EXISTS `coupons` (
    `coupon_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `coupon_name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    `store_id` BIGINT NOT NULL COMMENT '所属店铺ID(关联stores表)',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID(关联admins表)',
    `coupon_type` VARCHAR(2) NOT NULL DEFAULT '0' COMMENT '优惠券类型 0-满减券 1-折扣券',
    `discount_value` DECIMAL(10,2) NOT NULL COMMENT '优惠值(满减为金额,折扣为折扣比例如0.8表示8折)',
    `min_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '最低消费金额(满减门槛)',
    `total_count` INT NOT NULL COMMENT '发放总量',
    `remaining_count` INT NOT NULL COMMENT '剩余数量',
    `claim_limit` INT NOT NULL DEFAULT 1 COMMENT '每人限领数量',
    `start_time` DATETIME NOT NULL COMMENT '生效开始时间',
    `end_time` DATETIME NOT NULL COMMENT '生效结束时间',
    `status` VARCHAR(2) NOT NULL DEFAULT '0' COMMENT '状态 0-正常 1-已停用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`coupon_id`),
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 用户优惠券表(用户领取记录)
CREATE TABLE IF NOT EXISTS `user_coupons` (
    `user_coupon_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID(关联users表)',
    `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID(关联coupons表)',
    `status` VARCHAR(2) NOT NULL DEFAULT '0' COMMENT '状态 0-未使用 1-已使用 2-已过期',
    `used_order_id` BIGINT DEFAULT NULL COMMENT '使用的订单ID',
    `claim_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `used_time` DATETIME DEFAULT NULL COMMENT '使用时间',
    PRIMARY KEY (`user_coupon_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';
