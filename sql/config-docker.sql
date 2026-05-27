-- ============================================================
--  config-docker.sql —— Docker 部署专用 Nacos 配置初始化脚本
--  与 config.sql 的区别：
--    1. localhost / 127.0.0.1 → Docker 服务名（mysql / redis / minio / file-service）
--    2. 敏感值改为 ${ENV_VAR} 占位，由各容器的环境变量注入
--    3. MySQL useSSL=false & allowPublicKeyRetrieval=true（MySQL 8 兼容）
--  !! 本地开发仍使用 config.sql，此文件仅供 docker-compose 挂载 !!
-- ============================================================

DROP DATABASE IF EXISTS `nacos-config`;

CREATE DATABASE `nacos-config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `nacos-config`;

/******************************************/
/*   表名称 = config_info                 */
/******************************************/
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) DEFAULT NULL COMMENT 'group_id',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL COMMENT 'configuration description',
  `c_use` varchar(64) DEFAULT NULL COMMENT 'configuration usage',
  `effect` varchar(64) DEFAULT NULL COMMENT '配置生效的描述',
  `type` varchar(64) DEFAULT NULL COMMENT '配置的类型',
  `c_schema` text COMMENT '配置的模式',
  `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

INSERT INTO config_info(id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES

-- ① 全局共享配置（无变更）
(1,'application-dev.yml','DEFAULT_GROUP','spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: true\n      min-request-size: 8192\n    response:\n      enabled: true\n\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: ''*''\n','9928f41dfb10386ad38b3254af5692e0','2020-05-20 12:00:00','2024-08-29 12:14:45','nacos','0:0:0:0:0:0:0:1','','','common','null','null','yaml','',''),

-- ② 网关配置：redis host → redis 服务名，redis password 从环境变量注入
(2,'gateway-dev.yml','DEFAULT_GROUP','spring:\n  data:\n    redis:\n      host: redis\n      port: 6379\n      password: ${REDIS_PASSWORD:}\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        - id: admin-auth\n          uri: lb://admin-service\n          predicates:\n            - Path=/admin/auth/login,/admin/auth/register\n          filters:\n            - CacheRequestBody\n            - ValidateCodeFilter\n        - id: admin-service\n          uri: lb://admin-service\n          predicates:\n            - Path=/admin/**\n        - id: user-auth\n          uri: lb://user-service\n          predicates:\n            - Path=/user/auth/login,/user/auth/register\n          filters:\n            - CacheRequestBody\n            - ValidateCodeFilter\n        - id: user-service\n          uri: lb://user-service\n          predicates:\n            - Path=/user/**\n        - id: transaction-service\n          uri: lb://transaction-service\n          predicates:\n            - Path=/transaction/**\n        - id: file-service\n          uri: lb://file-service\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n\nsecurity:\n  captcha:\n    enabled: false\n    type: math\n  xss:\n    enabled: true\n    excludeUrls:\n      - /admin/notice\n  ignore:\n    whites:\n      - /admin/auth/login\n      - /admin/auth/register\n      - /admin/auth/emailCode\n      - /admin/auth/resetPassword\n      - /user/auth/login\n      - /user/auth/register\n      - /transaction/mock/wechat/pay/do-pay\n      - /csrf\n','','2020-05-14 14:17:55','2024-09-14 04:49:34','nacos','0:0:0:0:0:0:0:1','','','gateway','null','null','yaml','',''),

-- ③ admin-service：mysql → mysql 服务名，redis → redis 服务名，密码从环境变量注入
(3,'admin-service-dev.yml','DEFAULT_GROUP','spring:\n  data:\n    redis:\n      host: redis\n      port: 6379\n      password: ${REDIS_PASSWORD:}\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: admin123\n    dynamic:\n      primary: master\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://mysql:3306/mydb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true\n          username: root\n          password: ${DB_PASSWORD}\n\nmybatis-plus:\n  type-aliases-package: com.sql.common.entity\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n','','2020-11-20 00:00:00','2024-09-14 04:49:54','nacos','0:0:0:0:0:0:0:1','','','admin-service','null','null','yaml','',''),

-- ④ user-service：同 admin-service
(4,'user-service-dev.yml','DEFAULT_GROUP','spring:\n  data:\n    redis:\n      host: redis\n      port: 6379\n      password: ${REDIS_PASSWORD:}\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: admin123\n    dynamic:\n      primary: master\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://mysql:3306/mydb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true\n          username: root\n          password: ${DB_PASSWORD}\n\nmybatis-plus:\n  type-aliases-package: com.sql.common.entity\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n','','2020-11-20 00:00:00','2024-09-14 04:50:12','nacos','0:0:0:0:0:0:0:1','','','user-service','null','null','yaml','',''),

-- ⑤ transaction-service：同 admin-service
(5,'transaction-service-dev.yml','DEFAULT_GROUP','spring:\n  data:\n    redis:\n      host: redis\n      port: 6379\n      password: ${REDIS_PASSWORD:}\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: admin123\n    dynamic:\n      primary: master\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://mysql:3306/mydb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true\n          username: root\n          password: ${DB_PASSWORD}\n\nmybatis-plus:\n  type-aliases-package: com.sql.common.entity\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n','','2020-11-20 00:00:00','2024-09-14 04:49:42','nacos','0:0:0:0:0:0:0:1','','','transaction-service','null','null','yaml','',''),

-- ⑥ file-service：文件全部本地存储，路径改为容器内路径，domain 从环境变量注入
(6,'file-service-dev.yml','DEFAULT_GROUP','file:\n  domain: ${FILE_SERVICE_URL:http://localhost:9900}\n  path: /app/files\n  avatar-path: /app/pics/avatars\n  sign-path: /app/pics/signs\n  tp-path: /app/tps\n  tm-path: /app/tms\n  child-photo-path: /app/pics/photos/children\n  coach-photo-path: /app/pics/photos/coaches\n\nreferer:\n  enabled: false\n  allowed-domains: localhost,127.0.0.1\n','','2020-11-20 00:00:00','2025-09-02 05:10:11','nacos','0:0:0:0:0:0:0:1','','','file-service','null','null','yaml','',''),

-- ⑦ Sentinel 流控规则（无变更）
(7,'sentinel-gateway','DEFAULT_GROUP','[\n    {\n        \"resource\": \"admin-service\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n    {\n        \"resource\": \"user-service\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n    {\n        \"resource\": \"transaction-service\",\n        \"count\": 300,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n    {\n        \"resource\": \"file-service\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]','9f3a3069261598f74220bc47958ec252','2020-11-20 00:00:00','2020-11-20 00:00:00',NULL,'0:0:0:0:0:0:0:1','','','sentinel','null','null','json',NULL,'');


/******************************************/
/*   其余 Nacos 元数据表（结构与 config.sql 相同）*/
/******************************************/
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(255) NOT NULL,
  `datum_id` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `config_info_gray` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `src_user` text,
  `src_ip` varchar(100) DEFAULT NULL,
  `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_modified` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  `gray_name` varchar(128) NOT NULL,
  `gray_rule` text NOT NULL,
  `encrypted_data_key` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfogray_datagrouptenantgray` (`data_id`,`group_id`,`tenant_id`,`gray_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL,
  `content` longtext NOT NULL,
  `beta_ips` varchar(1024) DEFAULT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  `tag_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL,
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL,
  `tag_name` varchar(128) NOT NULL,
  `tag_type` varchar(64) DEFAULT NULL,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` varchar(128) NOT NULL DEFAULT '',
  `quota` int(10) unsigned NOT NULL DEFAULT '0',
  `usage` int(10) unsigned NOT NULL DEFAULT '0',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `his_config_info` (
  `id` bigint(20) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL,
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '',
  `publish_type` varchar(50) DEFAULT 'formal',
  `gray_name` varchar(50) DEFAULT NULL,
  `ext_info` longtext DEFAULT NULL,
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` varchar(128) NOT NULL DEFAULT '',
  `quota` int(10) unsigned NOT NULL DEFAULT '0',
  `usage` int(10) unsigned NOT NULL DEFAULT '0',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kp` varchar(128) NOT NULL,
  `tenant_id` varchar(128) DEFAULT '',
  `tenant_name` varchar(128) DEFAULT '',
  `tenant_desc` varchar(256) DEFAULT NULL,
  `create_source` varchar(32) DEFAULT NULL,
  `gmt_create` bigint(20) NOT NULL,
  `gmt_modified` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL PRIMARY KEY,
  `password` varchar(500) NOT NULL,
  `enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
  `username` varchar(50) NOT NULL,
  `role` varchar(50) NOT NULL,
  UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
  `role` varchar(50) NOT NULL,
  `resource` varchar(128) NOT NULL,
  `action` varchar(8) NOT NULL,
  UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

-- Nacos 默认账号 nacos/nacos（bcrypt 哈希）
INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);
INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');
