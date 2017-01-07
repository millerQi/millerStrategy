package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenterEnum;
import com.miller.priceMargin.model.moreCenterPriceMargin.SystemAllocation;
import com.miller.priceMargin.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-4.
 * 配置参数
 * 迁移头寸
 */
public class AllocationSource {

    //1 为btc 2 为 ltc
    static int coin;

    public static int depthSize = 1;

    public static String getCoinType() {
        if (coin == 1)
            return "btc";
        return "ltc";
    }

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


}
