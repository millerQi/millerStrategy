package com.miller.priceMargin.tradeCenter.huobi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public abstract class Base {

    private Log logger = LogFactory.getLog(Base.class);
    //火币现货配置信息
    public static String HUOBI_ACCESS_KEY = "8208e1f4-84460c06-d551b55a-39a46";
    public static String HUOBI_SECRET_KEY = "eb7e633b-1176943f-e74b11ab-bbac2";
    public static String HUOBI_API_URL = "https://api.huobi.com/apiv3";

    //bitvc现货，期货共用accessKey,secretKey配置信息
    public static String BITVC_ACCESS_KEY = "";
    public static String BITVC_SECRET_KEY = "";


    protected static int success = 200;


    public String post(Map<String, Object> map, String url) {
        try {
            return HttpUtil.post(url, map, response -> {
                int code = response.getStatusLine().getStatusCode();
                if (success == code) {
                    return EntityUtils.toString(response.getEntity(), "utf-8");
                }
                logger.info("response code {}" + code);
                return null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error post client";
    }

    public long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public String sign(TreeMap<String, Object> map) {
        StringBuilder inputStr = new StringBuilder();
        for (Map.Entry<String, Object> me : map.entrySet()) {
            inputStr.append(me.getKey()).append("=").append(me.getValue()).append("&");
        }
        return EncryptUtil.MD5(inputStr.substring(0, inputStr.length() - 1)).toLowerCase();
    }

    public abstract BigDecimal ticker();
}