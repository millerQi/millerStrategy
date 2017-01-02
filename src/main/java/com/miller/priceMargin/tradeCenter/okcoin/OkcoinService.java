package com.miller.priceMargin.tradeCenter.okcoin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miller.priceMargin.util.StringUtil;
import org.apache.http.HttpException;
import org.springframework.stereotype.Component;


/**
 * Created by Miller on 2017/1/1.
 */
@Component
public class OkcoinService {
    private String secret_key = "D67E022D0242FC3FE737C78BF8B546E4";
    private String api_key = "8a134f3f-51b8-4993-a916-bad635fdaf15";

    private String url_prex = "https://www.okcoin.cn";

    /**
     * 现货行情URL
     */
    private final String TICKER_URL = "/api/v1/ticker.do?";

    /**
     * 现货市场深度URL
     */
    private final String DEPTH_URL = "/api/v1/depth.do?";

    /**
     * 现货历史交易信息URL
     */
    private final String TRADES_URL = "/api/v1/trades.do?";

    /**
     * 现货获取用户信息URL
     */
    private final String USERINFO_URL = "/api/v1/userinfo.do?";

    /**
     * 现货 下单交易URL
     */
    private final String TRADE_URL = "/api/v1/trade.do?";

    /**
     * 现货 批量下单URL
     */
    private final String BATCH_TRADE_URL = "/api/v1/batch_trade.do";

    /**
     * 现货 撤销订单URL
     */
    private final String CANCEL_ORDER_URL = "/api/v1/cancel_order.do";

    /**
     * 现货 获取用户订单URL
     */
    private final String ORDER_INFO_URL = "/api/v1/order_info.do";

    /**
     * 现货 批量获取用户订单URL
     */
    private final String ORDERS_INFO_URL = "/api/v1/orders_info.do";

    /**
     * 现货 获取历史订单信息，只返回最近七天的信息URL
     */
    private final String ORDER_HISTORY_URL = "/api/v1/order_history.do";


    public BigDecimal ticker(String symbol) {
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();
        String param = "";
        if (!StringUtil.isEmpty(symbol)) {
            if (!param.equals("")) {
                param += "&";
            }
            param += "symbol=" + symbol;
        }
        String result = httpUtil.requestHttpGet(url_prex, TICKER_URL, param);
        return JSON.parseObject(result).getJSONObject("ticker").getBigDecimal("last");
    }

    public String depth(String symbol) throws HttpException, IOException {
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();
        String param = "";
        if (!StringUtil.isEmpty(symbol)) {
            if (!param.equals("")) {
                param += "&";
            }
            param += "symbol=" + symbol;
        }
        return httpUtil.requestHttpGet(url_prex, this.DEPTH_URL, param);
    }

    public String trades(String symbol, String since) {
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();
        String param = "";
        if (!StringUtil.isEmpty(symbol)) {
            if (!param.equals("")) {
                param += "&";
            }
            param += "symbol=" + symbol;
        }
        if (!StringUtil.isEmpty(since)) {
            if (!param.equals("")) {
                param += "&";
            }
            param += "since=" + since;
        }
        return httpUtil.requestHttpGet(url_prex, this.TRADES_URL, param);
    }

    public String userinfo(){
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.USERINFO_URL,
                params);
    }

    public String trade(String symbol, String type,
                        String price, String amount) {
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        if (!StringUtil.isEmpty(symbol)) {
            params.put("symbol", symbol);
        }
        if (!StringUtil.isEmpty(type)) {
            params.put("type", type);
        }
        if (!StringUtil.isEmpty(price)) {
            params.put("price", price);
        }
        if (!StringUtil.isEmpty(amount)) {
            params.put("amount", amount);
        }
        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.TRADE_URL,
                params);
    }

    public String batch_trade(String symbol, String type,
                              String orders_data) throws HttpException, IOException {
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        if (!StringUtil.isEmpty(symbol)) {
            params.put("symbol", symbol);
        }
        if (!StringUtil.isEmpty(type)) {
            params.put("type", type);
        }
        if (!StringUtil.isEmpty(orders_data)) {
            params.put("orders_data", orders_data);
        }
        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.BATCH_TRADE_URL,
                params);
    }

    public String cancel_order(String symbol, String order_id) throws HttpException, IOException {
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        if (!StringUtil.isEmpty(symbol)) {
            params.put("symbol", symbol);
        }
        if (!StringUtil.isEmpty(order_id)) {
            params.put("order_id", order_id);
        }

        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.CANCEL_ORDER_URL,
                params);
    }

    public String order_info(String symbol, long order_id) {
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        if (!StringUtil.isEmpty(symbol)) {
            params.put("symbol", symbol);
        }
        if (order_id != 0) {
            params.put("order_id", String.valueOf(order_id));
        }

        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.ORDER_INFO_URL,
                params);
    }

    public String orders_info(String type, String symbol,
                              String order_id) throws HttpException, IOException {
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        if (!StringUtil.isEmpty(type)) {
            params.put("type", type);
        }
        if (!StringUtil.isEmpty(symbol)) {
            params.put("symbol", symbol);
        }
        if (!StringUtil.isEmpty(order_id)) {
            params.put("order_id", order_id);
        }

        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.ORDERS_INFO_URL,
                params);
    }

    public String order_history(String symbol, String status,
                                String current_page, String page_length) throws HttpException, IOException {
        // 构造参数签名
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        if (!StringUtil.isEmpty(symbol)) {
            params.put("symbol", symbol);
        }
        if (!StringUtil.isEmpty(status)) {
            params.put("status", status);
        }
        if (!StringUtil.isEmpty(current_page)) {
            params.put("current_page", current_page);
        }
        if (!StringUtil.isEmpty(page_length)) {
            params.put("page_length", page_length);
        }

        String sign = MD5Util.buildMysignV1(params, this.secret_key);
        params.put("sign", sign);

        // 发送post请求
        HttpUtilManager httpUtil = HttpUtilManager.getInstance();

        return httpUtil.requestHttpPost(url_prex, this.ORDER_HISTORY_URL,
                params);
    }

    public static void main(String[] args) {
        JSONObject object = JSON.parseObject("{\"orders\":[{\"amount\":0.01,\"avg_price\":6928.4,\"create_date\":1483279857000,\"deal_amount\":0.01,\"order_id\":7658242703,\"orders_id\":7658242703,\"price\":6000,\"status\":2,\"symbol\":\"btc_cny\",\"type\":\"sell\"}],\"result\":true}");
        JSONObject object1 = object.getJSONArray("orders").getJSONObject(0);
        System.out.println();
    }
}
