CREATE TABLE `order_gain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sell_order_id` bigint(20) DEFAULT NULL,
  `buy_order_id` bigint(20) DEFAULT NULL,
  `gains` decimal(10,4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=latin1


CREATE TABLE `initialization_data` (
  `gains` decimal(10,4) DEFAULT NULL,
  `has_coin` int(1) DEFAULT NULL,
  `ok_free_price` decimal(10,4) DEFAULT NULL,
  `hb_free_price` decimal(10,4) DEFAULT NULL,
  `ok_last_price` decimal(10,4) DEFAULT NULL,
  `hb_last_price` decimal(10,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1


CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trade_center` varchar(10) DEFAULT NULL,
  `trade_direction` varchar(4) DEFAULT NULL,
  `ticker_price` decimal(10,4) DEFAULT NULL,
  `amount` decimal(10,4) DEFAULT NULL,
  `deal_amount` decimal(10,4) DEFAULT NULL,
  `deal_price` decimal(10,4) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=355 DEFAULT CHARSET=latin1