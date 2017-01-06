package com.miller.priceMargin.service;

import com.miller.priceMargin.model.moreCenterPriceMargin.SystemStatus;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-6.
 */
public interface SystemStatusService {
    /**
     * 是否存在数据
     * 存在返回1
     * 不存在返回-1
     */
    int existData();

    void save(SystemStatus systemStatus);

    /**
     * 修改成功 返回1
     * 失败 -1
     */
    int updateGains(BigDecimal gains, BigDecimal coinSellCount);
}
