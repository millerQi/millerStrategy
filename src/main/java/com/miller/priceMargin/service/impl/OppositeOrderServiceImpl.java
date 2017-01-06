package com.miller.priceMargin.service.impl;

import com.miller.priceMargin.model.moreCenterPriceMargin.OppositeOrder;
import com.miller.priceMargin.service.OppositeOrderService;
import com.miller.priceMargin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by tonyqi on 17-1-6.
 */
@Service
public class OppositeOrderServiceImpl implements OppositeOrderService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveOppositeOrder(OppositeOrder oppositeOrder) {
        String sql = "INSERT INTO oppsite_order (sell_center, sell_avg_price, sell_amount, buy_center, buy_avg_price, buy_amount, trade_status, gains, create_time) VALUES (?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, oppositeOrder.getSellCenter(), oppositeOrder.getSellAvgPrice(), oppositeOrder.getSellAmount(),
                oppositeOrder.getBuyCenter(), oppositeOrder.getBuyAvgPrice(), oppositeOrder.getBuyAmount(), oppositeOrder.isTradeStatus(), DateUtil.getTimestamp());
    }
}
