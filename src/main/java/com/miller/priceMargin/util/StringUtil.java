package com.miller.priceMargin.util;

/**
 * Created by Miller on 2017/1/1.
 */
public class StringUtil {
    public static boolean isEmpty(String var) {
        return var == null || var.length() == 0;
    }

    public static String getString(Object sellCenter) {
        return sellCenter == null ? null : (String) sellCenter;
    }
}
