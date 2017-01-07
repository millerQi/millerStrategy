package com.miller.priceMargin.service.impl;

import com.miller.priceMargin.model.moreCenterPriceMargin.TradeCenter;
import com.miller.priceMargin.service.TradeCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-6.
 */
@Service
public class TradeCenterServiceImpl implements TradeCenterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int existCenter(String center) {
        Assert.notNull(center, "center_name can not be null!");
        String sql = "select center_name from trade_center where center_name = '" + center + "' limit 1";
        String ret = jdbcTemplate.queryForObject(sql, String.class);
        if (ret == null)
            return 0;
        return 1;
    }

    @Override
    public void saveTradeCenter(TradeCenter tradeCenter) {
        Assert.notNull(tradeCenter, "tradeCenter can not be null!");
        String sql = "INSERT INTO trade_center(center_name, net_asset, free_asset, free_amount, borrow_amount, borrow_price) VALUES (?,?,?,?,?,?)";
        jdbcTemplate.update(sql, tradeCenter.getCenterName(), tradeCenter.getNetAsset(), tradeCenter.getFreeAsset()
                , tradeCenter.getFreeAmount(), tradeCenter.getBorrowAmount(), tradeCenter.getBorrowPrice());
    }

    @Override
    public int updateAsset(String centerName, BigDecimal freeAmount, BigDecimal freeAsset) {
        Assert.notNull(centerName, "center_name can not be null!");
        Assert.notNull(freeAmount, "free_amount can not be null!");
        Assert.notNull(freeAsset, "free_price can not be null!");
        String sql = "update trade_center set free_amount =  ? ,free_asset =  ? where center_name = ?";
        return jdbcTemplate.update(sql, freeAmount, freeAsset, centerName);
    }

    @Override
    public void truncateTable() {
        String dropTable = "TRUNCATE trade_center";
        jdbcTemplate.execute(dropTable);
    }

    @Override
    public BigDecimal getFreeAmount(String sellCenter) {
        Assert.notNull(sellCenter, "sell_center can not be null!");
        String sql = "select free_amount from trade_center where center_name = '" + sellCenter + "' limit 1";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    @Override
    public BigDecimal getFreePrice(String buyCenter) {
        Assert.notNull(buyCenter, "buy_center can not be null!");
        String sql = "select free_asset from trade_center where center_name = '" + buyCenter + "' limit 1";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    @Override
    public void updateNetAsset(BigDecimal netAsset, String centerName) {
        Assert.notNull(netAsset, "net_asset can not be null!");
        Assert.notNull(centerName, "centerName can not be null!");
        String sql = "update trade_center set net_asset = ? where center_name = ?";
        jdbcTemplate.update(sql, netAsset, centerName);
    }
}
