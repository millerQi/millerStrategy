package com.miller.priceMargin.service.impl;

import com.miller.priceMargin.model.moreCenterPriceMargin.SystemStatus;
import com.miller.priceMargin.service.SystemStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by tonyqi on 17-1-6.
 */
@Service
public class SystemStatusServiceImpl implements SystemStatusService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int existData() {
        String sql = "select count(*) from system_status";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public void save(SystemStatus systemStatus) {
        Assert.notNull(systemStatus, "systemStatus can not be null!");
        String sql = "INSERT INTO system_status (all_gains, coin_sell_count, gains_order_count, loss_order_count)" +
                " VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, systemStatus.getAllGains(), systemStatus.getCoinSellCount(), systemStatus.getGainsOrderCount(),
                systemStatus.getLossOrderCount());
    }

    @Override
    public int updateGains(BigDecimal gains, BigDecimal coinSellCount) {
        Assert.notNull(gains, "gains can not be null!");
        Assert.notNull(coinSellCount, "coinSellCount can not be null!");
        StringBuilder sql = new StringBuilder("UPDATE system_status SET all_gains = all_gains + ?, coin_sell_count = coin_sell_count + ?");
        if (gains.compareTo(BigDecimal.ZERO) >= 0)
            sql.append(",gains_order_count = gains_order_count + 1");
        else
            sql.append(",loss_order_count = loss_order_count + 1");
        return jdbcTemplate.update(sql.toString(), gains, coinSellCount);
    }

    @Override
    public SystemStatus getSystemStatus() {
        String existSql = "select count(*) from system_status";
        int ret = jdbcTemplate.queryForObject(existSql, Integer.class);
        if (ret == 0)
            return null;
        String sql = "select * from system_status limit 1";
        SystemStatus systemStatus = new SystemStatus();
        jdbcTemplate.query(sql, resultSet -> {
            systemStatus.setCoinSellCount(resultSet.getBigDecimal("coin_sell_count"));
            systemStatus.setAllGains(resultSet.getBigDecimal("all_gains"));
            systemStatus.setLossOrderCount(resultSet.getInt("loss_order_count"));
            systemStatus.setGainsOrderCount(resultSet.getInt("gains_order_count"));
        });
        return systemStatus;
    }
}
