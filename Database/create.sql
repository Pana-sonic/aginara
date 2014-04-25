CREATE DATABASE IF NOT EXISTS `Bizeli` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `Bizeli`;

CREATE TABLE IF NOT EXISTS `prices` (
	  `pdate` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `region` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `market` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `id` int(11) NOT NULL,
	  `barcode` varchar(30),
	  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `price` float(12) NOT NULL
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
