package com.miller.priceMargin.service.impl;

import com.miller.priceMargin.model.moreCenterPriceMargin.SystemAllocation;
import com.miller.priceMargin.service.SystemAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by tonyqi on 17-1-6.
 */
@Service
public class SystemAllocationServiceImpl implements SystemAllocationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveSystemAllocation(SystemAllocation systemAllocation) {
        String sql = "insert into system_allocation(coin, price_margin, reverse_price_margin, tick_amount, reverse_multiple_amount, strategy_open) VALUES (?,?,?,?,?,?)";
        jdbcTemplate.update(sql, systemAllocation.getCoin(), systemAllocation.getPriceMargin(),
                systemAllocation.getReversePriceMargin(), systemAllocation.getTickAmount(), systemAllocation.getReverseMultipleAmount(), systemAllocation.isStrategyOpen());
    }

    @Override
    public SystemAllocation getSystemAllocation() {
        String existSql = "select count(*) from system_allocation";
        int ret = jdbcTemplate.queryForObject(existSql, Integer.class);
        if (ret == 0)
            return null;
        String sql = "select * from system_allocation limit 1";
        SystemAllocation system = new SystemAllocation();
        jdbcTemplate.query(sql, resultSet -> {
            system.setCoin(resultSet.getInt("coin"));
            system.setPriceMargin(resultSet.getBigDecimal("price_margin"));
            system.setStrategyOpen(resultSet.getBoolean("strategy_open"));
            system.setReverseMultipleAmount(resultSet.getFloat("reverse_multiple_amount"));
            system.setTickAmount(resultSet.getBigDecimal("tick_amount"));
            system.setReversePriceMargin(resultSet.getBigDecimal("reverse_price_margin"));
        });
        return system;
    }
}
