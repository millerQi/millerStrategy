package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-4.
 * 配置参数
 */
class AllocationSource {
    static boolean strategyOpen = true;
    /*盈利价差*/
    static BigDecimal price_margin = BigDecimal.ONE;

    /*迁移头寸，可接受的最大亏损 以一个币种为单位*/
    static BigDecimal canReversePriceM = BigDecimal.valueOf(0.3);

    //分批挂单量
    static BigDecimal tickAmount;


    /*持币情况，程序启动赋值*/
    static BigDecimal okFreePrice;

    static BigDecimal okFreeAmount;

    static BigDecimal hbFreePrice;

    static BigDecimal hbFreeAmount;
}
