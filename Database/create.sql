CREATE DATABASE IF NOT EXISTS `Bizeli` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `Bizeli`;

CREATE TABLE IF NOT EXISTS `prices` (
	  `Ημερομηνια` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `Νομός___Δήμος` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `Κατάστημα` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `Κωδικός_Προϊόντος` int(11) NOT NULL,
	  `Barcode` varchar(30),
	  `Όνομα_Προϊόντος` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
	  `Τιμή` float(12) NOT NULL
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
