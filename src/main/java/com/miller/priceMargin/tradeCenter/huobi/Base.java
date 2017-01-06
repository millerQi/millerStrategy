package com.miller.priceMargin.tradeCenter.huobi;

import com.miller.priceMargin.util.StringUtil;
import com.miller.priceMargin.util.URLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class Base {

    private Log logger = LogFactory.getLog(Base.class);
    //火币现货配置信息
    static String HUOBI_ACCESS_KEY = "8208e1f4-84460c06-d551b55a-39a46";
    static String HUOBI_SECRET_KEY = "eb7e633b-1176943f-e74b11ab-bbac2";
    static String HUOBI_API_URL = "https://api.huobi.com/apiv3";

    //bitvc现货，期货共用accessKey,secretKey配置信息
    public static String BITVC_ACCESS_KEY = "";
    public static String BITVC_SECRET_KEY = "";


    protected static int success = 200;


    String post(Map<String, Object> map, String url) {
        Map<String, String> newMap = new HashMap<>();
        if (map.size() != 0) {
            Set<String> keys = map.keySet();
            for (String key : keys)
                newMap.put(key, StringUtil.getString(map.get(key)));
        }
        return URLUtil.doPost(url, newMap);
    }

    long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    String sign(TreeMap<String, Object> map) {
        StringBuilder inputStr = new StringBuilder();
        for (Map.Entry<String, Object> me : map.entrySet()) {
            inputStr.append(me.getKey()).append("=").append(me.getValue()).append("&");
        }
        return EncryptUtil.MD5(inputStr.substring(0, inputStr.length() - 1)).toLowerCase();
    }

    public abstract BigDecimal ticker();
}