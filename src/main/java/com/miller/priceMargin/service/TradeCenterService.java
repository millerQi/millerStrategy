package com.miller.priceMargin.service;

import com.miller.priceMargin.model.moreCenterPriceMargin.TradeCenter;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-6.
 */
public interface TradeCenterService {

    /**
     * 检测交易中心是否存在
     * 1 存在
     * 0 不存在
     */
    int existCenter(String center);

    void saveTradeCenter(TradeCenter tradeCenter);

    int updateAsset(String centerName, BigDecimal freeAmount, BigDecimal freeAsset);

    void truncateTable();

    BigDecimal getFreeAmount(String sellCenter);

    BigDecimal getFreePrice(String buyCenter);

    void updateNetAsset(BigDecimal netAsset, String centerName);

    /**
     * 增加或减少
     *
     * @param center
     * @param freePrice
     * @param amount
     */
    void updateAmount(String center, BigDecimal freePrice, BigDecimal amount);
}
