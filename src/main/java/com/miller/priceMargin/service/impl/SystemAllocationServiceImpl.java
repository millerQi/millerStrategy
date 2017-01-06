package com.miller.priceMargin.service.impl;

import com.miller.priceMargin.model.moreCenterPriceMargin.SystemAllocation;
import com.miller.priceMargin.service.SystemAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by tonyqi on 17-1-6.
 */
@Service
public class SystemAllocationServiceImpl implements SystemAllocationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int updateSystemAllocation(SystemAllocation systemAllocation) {
        String sql = "UPDATE system_allocation " +
                "SET coin = ?, price_margin = ?, reverse_price_margin = ?, tick_amount = ? , reverse_multiple_amount = ?";
        return jdbcTemplate.update(sql, systemAllocation.getCoin(), systemAllocation.getPriceMargin(),
                systemAllocation.getReversePriceMargin(), systemAllocation.getTickAmount(), systemAllocation.getReverseMultipleAmount());
    }

    @Override
    public SystemAllocation getSystemAllocation() {
        String sql = "select * from system_allocation limit 1";
        return jdbcTemplate.queryForObject(sql, SystemAllocation.class);
    }
}
