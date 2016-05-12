<!--user_info增加身份证号、身份证图片地址、营业执照图片地址、年度营业额字段-->
ALTER TABLE user_info ADD id_card VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
      ADD id_card_url VARCHAR (64) DEFAULT NULL COMMENT '身份证图片地址',
      ADD business_license_url VARCHAR (64) DEFAULT NULL COMMENT '营业执照图片地址',
      ADD annual_sales VARCHAR (100) DEFAULT NULL COMMENT '年度营业额',
      ADD longitude VARCHAR (64) DEFAULT NULL COMMENT '经度',
      ADD latitude VARCHAR (64) DEFAULT NULL COMMENT '纬度'

<!--user_info增加绑定码字段-->
ALTER TABLE user_info ADD binding_code VARCHAR (10) DEFAULT NULL COMMENT '绑定码'

<!--buyer_receive_address增加是否为默认地址、经度、纬度字段-->
ALTER TABLE buyer_receive_address ADD isdefault tinyint(1) DEFAULT NULL COMMENT '是否为默认地址',
      ADD longitude DOUBLE (10,2) DEFAULT NULL COMMENT '经度',
      ADD latitude DOUBLE (10,2) DEFAULT NULL COMMENT '纬度'

<!--创建帮助中心表-->
DROP TABLE IF EXISTS `help_center`;
CREATE TABLE `help_center` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) NOT NULL COMMENT '标题或问题',
  `content` varchar(256) DEFAULT NULL COMMENT '内容或回答',
  `type` int(2) DEFAULT NULL COMMENT '类型（保留字段）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

<!--创建app版本表-->
DROP TABLE IF EXISTS `app_version`;
CREATE TABLE `app_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` varchar(20) NOT NULL COMMENT '版本',
  `url` varchar(100) DEFAULT NULL COMMENT '文件地址',
  `type` int(2) NOT NULL COMMENT 'APP类型 1：android 2：ios',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态（保留字段）',
  `content` varchar(255) DEFAULT NULL COMMENT '版本说明',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

<!--创建app导航表-->
DROP TABLE IF EXISTS `app_nav`;
CREATE TABLE `app_home_nav` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `img` varchar(256) DEFAULT NULL COMMENT '导航图片',
  `type` varchar(255) DEFAULT NULL COMMENT '类型（保留字段）',
  `value` varchar(255) DEFAULT NULL COMMENT '值',
  `custom_val` varchar(255) DEFAULT NULL COMMENT '导航菜单指向',
  `app_home_nav` int(11) DEFAULT NULL COMMENT '图标分类',
  `sort` int(11) DEFAULT NULL COMMENT '排序字段',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `app_type` int(11) DEFAULT '1' COMMENT 'app类型(1：买家APP 2：卖家APP)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;