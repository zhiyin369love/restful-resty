<!--user_info增加身份证号、身份证图片地址、营业执照图片地址、年度营业额字段-->
ALTER TABLE user_info ADD id_card VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
      ADD id_card_url VARCHAR (64) DEFAULT NULL COMMENT '身份证图片地址',
      ADD business_license_url VARCHAR (64) DEFAULT NULL COMMENT '营业执照图片地址',
      ADD annual_sales VARCHAR (100) DEFAULT NULL COMMENT '年度营业额'

<!--buyer_receive_address增加是否为默认地址字段-->
ALTER TABLE buyer_receive_address ADD isdefault tinyint(1) DEFAULT NULL COMMENT '是否为默认地址'

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