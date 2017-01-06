package com.miller.priceMargin.service.impl;

import com.miller.priceMargin.model.moreCenterPriceMargin.LogInfo;
import com.miller.priceMargin.service.LogInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by tonyqi on 17-1-6.
 */
@Service
public class LogInfoServiceImpl implements LogInfoService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveLogInfo(LogInfo logInfo) {
        String sql = "INSERT INTO log_info (log_msg, warn) VALUES (?, ?)";
        jdbcTemplate.update(sql, logInfo.getLogMsg(), logInfo.isWarn());
    }
}
