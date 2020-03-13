/*
MySQL Backup
Database: seckill
Backup Time: 2020-03-12 20:02:34
*/
CREATE DATABASE /*!32312 IF NOT EXISTS*/ `seckill` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `seckill`;

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `seckill`.`item`;
DROP TABLE IF EXISTS `seckill`.`item_stock`;
DROP TABLE IF EXISTS `seckill`.`order_info`;
DROP TABLE IF EXISTS `seckill`.`promo`;
DROP TABLE IF EXISTS `seckill`.`sequence_info`;
DROP TABLE IF EXISTS `seckill`.`user_info`;
DROP TABLE IF EXISTS `seckill`.`user_password`;
CREATE TABLE `item` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品id、主键',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '商品名称',
  `price` decimal(10,0) NOT NULL DEFAULT '0' COMMENT '商品价格',
  `sales` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品销量',
  `img_url` varchar(250) NOT NULL DEFAULT '' COMMENT '商品图片URL',
  PRIMARY KEY (`id`),
  UNIQUE KEY `title_unique_inde` (`title`) USING BTREE COMMENT '标题不能重复'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品信息表';
CREATE TABLE `item_stock` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品库存id、主键',
  `stock` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品库存',
  `item_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品库存表';
CREATE TABLE `order_info` (
  `id` varchar(32) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `item_price` double NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  `order_price` double NOT NULL DEFAULT '0',
  `promo_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `promo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(255) NOT NULL DEFAULT '',
  `start_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `promo_item_price` double NOT NULL DEFAULT '0',
  `end_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
CREATE TABLE `sequence_info` (
  `name` varchar(255) NOT NULL,
  `current_value` int(11) NOT NULL DEFAULT '0',
  `step` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `age` int(11) NOT NULL DEFAULT '0',
  `gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '//1代表男性，2代表女性',
  `telephone` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `register_mode` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '//byphone,bywechat,byalipay',
  `third_party_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telephone_unique_index` (`telephone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrpt_password` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
BEGIN;
LOCK TABLES `seckill`.`item` WRITE;
DELETE FROM `seckill`.`item`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `seckill`.`item_stock` WRITE;
DELETE FROM `seckill`.`item_stock`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `seckill`.`order_info` WRITE;
DELETE FROM `seckill`.`order_info`;
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `seckill`.`promo` WRITE;
DELETE FROM `seckill`.`promo`;
INSERT INTO `seckill`.`promo` (`id`,`promo_name`,`start_date`,`item_id`,`promo_item_price`,`end_date`) VALUES (1, 'iphone11抢购活动', '2020-01-19 00:04:30', 6, 100, '2020-12-31 00:00:00');
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `seckill`.`sequence_info` WRITE;
DELETE FROM `seckill`.`sequence_info`;
INSERT INTO `seckill`.`sequence_info` (`name`,`current_value`,`step`) VALUES ('order_info', 42, 1);
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `seckill`.`user_info` WRITE;
DELETE FROM `seckill`.`user_info`;
INSERT INTO `seckill`.`user_info` (`id`,`name`,`age`,`gender`,`telephone`,`register_mode`,`third_party_id`) VALUES (1, 'hackhu', 0, 1, '88888888', '1', 'bywechat');
UNLOCK TABLES;
COMMIT;
BEGIN;
LOCK TABLES `seckill`.`user_password` WRITE;
DELETE FROM `seckill`.`user_password`;
INSERT INTO `seckill`.`user_password` (`id`,`encrpt_password`,`user_id`) VALUES (1, 'ddwijqieq', 1);
UNLOCK TABLES;
COMMIT;
