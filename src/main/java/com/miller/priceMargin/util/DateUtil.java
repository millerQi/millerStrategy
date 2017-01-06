package com.miller.priceMargin.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by tonyqi on 17-1-6.
 */
public class DateUtil {
    public static Timestamp getTimestamp() {
        return new Timestamp(new Date().getTime());
    }
}
