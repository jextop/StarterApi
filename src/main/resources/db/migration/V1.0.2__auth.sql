CREATE TABLE if not EXISTS `auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `description` longtext,
  `app_key` varchar(64) NOT NULL,
  `secret` longtext NOT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `auth_id` bigint(20) DEFAULT NULL,
  `created` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `updated` datetime(6) DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_key` (`app_key`),
  KEY `auth_is_deleted_2cf7df8c` (`is_deleted`),
  KEY `auth_auth_id_057810df` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
