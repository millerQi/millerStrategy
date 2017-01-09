CREATE TABLE `log_info` (
  `log_msg` text,
  `warn` bit(1) DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1

CREATE TABLE `opposite_order` (
  `sell_center` varchar(7) DEFAULT NULL,
  `sell_avg_price` decimal(7,2) DEFAULT NULL,
  `sell_amount` decimal(7,2) DEFAULT NULL,
  `buy_center` varchar(7) DEFAULT NULL,
  `buy_avg_price` decimal(7,2) DEFAULT NULL,
  `buy_amount` decimal(7,2) DEFAULT NULL,
  `trade_status` bit(1) DEFAULT b'0',
  `gains` decimal(10,4) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1

CREATE TABLE `system_allocation` (
  `coin` int(1) DEFAULT NULL,
  `price_margin` decimal(7,4) DEFAULT NULL,
  `reverse_price_margin` decimal(7,4) DEFAULT NULL,
  `tick_amount` decimal(7,2) DEFAULT NULL,
  `reverse_multiple_amount` float(3,1) DEFAULT NULL,
  `strategy_open` bit(1) DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1

CREATE TABLE `system_status` (
  `all_gains` decimal(10,4) DEFAULT NULL,
  `coin_sell_count` decimal(12,4) DEFAULT NULL,
  `gains_order_count` int(7) DEFAULT NULL,
  `loss_order_count` int(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1

CREATE TABLE `trade_center` (
  `center_name` varchar(7) DEFAULT NULL,
  `net_asset` decimal(10,4) DEFAULT NULL,
  `free_asset` decimal(10,4) DEFAULT NULL,
  `free_amount` decimal(10,4) DEFAULT NULL,
  `borrow_amount` decimal(10,4) DEFAULT NULL,
  `borrow_price` decimal(10,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1