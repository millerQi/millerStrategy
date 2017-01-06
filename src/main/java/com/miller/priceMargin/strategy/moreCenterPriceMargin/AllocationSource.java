package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenter;
import com.miller.priceMargin.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-4.
 * 配置参数
 * 迁移头寸
 */
public class AllocationSource {
    static boolean strategyOpen = true;
    /*盈利价差*/
    static BigDecimal price_margin = BigDecimal.valueOf(0.02);

    /*迁移头寸，可接受的最大亏损 以一个币种为单位*/
    static BigDecimal canReversePriceM = BigDecimal.valueOf(0);

    //分批挂单量
    static BigDecimal tickAmount = BigDecimal.valueOf(1);

    //1 为btc 2 为 ltc
    static int coin = 2;

    public static int depthSize = 1;

    public static String getCoinType() {
        if (coin == 1)
            return "btc";
        return "ltc";
    }


    /*持币情况，程序启动赋值*/
    static BigDecimal okFreePrice;

    static BigDecimal okFreeBTCAmount;

    static BigDecimal hbFreePrice;

    static BigDecimal hbFreeBTCAmount;

    static BigDecimal okFreeLTCAmount;

    static BigDecimal hbFreeLTCAmount;

    /*是否迁移头寸*/
    private static boolean isReverse;
    /*迁移目标交易所*/
    private static String reverseCenter;

    static synchronized String getReverseCenter() {
        return reverseCenter;
    }

    static synchronized void setReverseCenter(String center) {
        AllocationSource.reverseCenter = center;
    }

    static synchronized boolean getReverse() {
        return isReverse;
    }

    static synchronized void setReverse(boolean isReverse) {
        AllocationSource.isReverse = isReverse;
    }

    static synchronized BigDecimal getFreePrice(String tradeCenter) {
        if (StringUtil.isEmpty(tradeCenter))
            return BigDecimal.ZERO;
        if (tradeCenter.equals(TradeCenter.okcoin.name()))
            return okFreePrice;
        else if (tradeCenter.equals(TradeCenter.huobi.name()))
            return hbFreePrice;
        else
            return BigDecimal.ZERO;
    }

    static synchronized BigDecimal getFreeAmount(String tradeCenter) {
        if (StringUtil.isEmpty(tradeCenter))
            return BigDecimal.ZERO;
        if (tradeCenter.equals(TradeCenter.okcoin.name())) {
            if (coin == 1)
                return okFreeBTCAmount;
            else
                return okFreeLTCAmount;
        } else if (tradeCenter.equals(TradeCenter.huobi.name()))
            if (coin == 1)
                return hbFreeBTCAmount;
            else
                return hbFreeLTCAmount;
        else
            return BigDecimal.ZERO;
    }

    static synchronized void addFreeAmount(String tradeCenter, int coin, BigDecimal amount) {
        if (coin == 1) {
            if (tradeCenter.equals(TradeCenter.huobi.name()))
                okFreeBTCAmount = okFreeBTCAmount.add(amount);
            else if (tradeCenter.equals(TradeCenter.okcoin.name()))
                okFreeBTCAmount = okFreeBTCAmount.add(amount);
        } else {
            if (tradeCenter.equals(TradeCenter.okcoin.name()))
                okFreeLTCAmount = okFreeLTCAmount.add(amount);
            else if (tradeCenter.equals(TradeCenter.huobi.name()))
                hbFreeLTCAmount = hbFreeLTCAmount.add(amount);
        }
    }

    static long startTime;
    static int gainsOrderCount;
    static int noGainsOrderCount;
    static BigDecimal noGains = BigDecimal.ZERO;//总亏损

}
