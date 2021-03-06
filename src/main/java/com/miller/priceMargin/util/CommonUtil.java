package com.miller.priceMargin.util;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-4.
 */
public class CommonUtil {

    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getMuchSmallPrice(BigDecimal price) {
        return String.valueOf(price.multiply(BigDecimal.valueOf(0.98)).setScale(2, BigDecimal.ROUND_DOWN));
    }

    public static String getMuchBigPrice(BigDecimal price) {
        return String.valueOf(price.multiply(BigDecimal.valueOf(1.02)).setScale(2, BigDecimal.ROUND_DOWN));
    }

    public static BigDecimal getDecimalMuchSmallPrice(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.98)).setScale(2, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal getDecimalMuchBigPrice(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(1.02)).setScale(2, BigDecimal.ROUND_DOWN);
    }
}
