package com.miller.priceMargin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miller.priceMargin.enumUtil.TradeCenter;
import com.miller.priceMargin.model.order.OrderInfo;
import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.model.order.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by Miller on 2017/1/1.
 */
@Component
public class APIResultHandle {

    private Log log = LogFactory.getLog(APIResultHandle.class);

    public TradeInfo getTradeInfo(String result, String center) {
        if (StringUtil.isEmpty(center) || StringUtil.isEmpty(result))
            return null;
        JSONObject object = JSON.parseObject(result);
        if (center.equals(TradeCenter.okcoin.name())) {
            Long id = object.getLong("order_id");
            if (id == null) {
                log.error("Trade failed :" + result);
                return null;
            }
            return new TradeInfo(id, object.getString("result"));
        } else if (center.equals(TradeCenter.huobi.name())) {
            Long id = object.getLong("id");
            if (id == null) {
                log.error("Trade failed :" + result);
                return null;
            }
            return new TradeInfo(id, object.getString("result"));
        }
        return null;
    }

    public OrderInfo getOrderInfo(String result, String center) {
        if (StringUtil.isEmpty(result) || StringUtil.isEmpty(center))
            return null;
        JSONObject o = JSON.parseObject(result);
        OrderInfo orderInfo = new OrderInfo();
        if (center.equals(TradeCenter.okcoin.name())) {
            orderInfo.setResult(o.getString("result"));
            o = o.getJSONArray("orders").getJSONObject(0);
            orderInfo.setAmount(o.getBigDecimal("amount"));
            orderInfo.setAvgPrice(o.getBigDecimal("avg_price"));
            orderInfo.setDealAmount(o.getBigDecimal("deal_amount"));
            orderInfo.setPrice(o.getBigDecimal("price"));
            orderInfo.setTradeDirection(o.getString("type"));
            return orderInfo;
        } else if (center.equals(TradeCenter.huobi.name())) {
            orderInfo.setAmount(o.getBigDecimal("order_amount"));
            orderInfo.setAvgPrice(o.getBigDecimal("processed_price"));
            orderInfo.setDealAmount(o.getBigDecimal("processed_amount"));
            orderInfo.setPrice(o.getBigDecimal("order_price"));
            int type = o.getInteger("type");
            String direction;
            if (type == 1 || type == 3)
                direction = "buy";
            else
                direction = "sell";
            orderInfo.setTradeDirection(direction);
            orderInfo.setResult("true");
            return orderInfo;
        }
        return null;
    }


    public BigDecimal getNetAsset(String ret, String center) {
        if (StringUtil.isEmpty(ret) || StringUtil.isEmpty(center))
            return null;
        JSONObject object = JSON.parseObject(ret);
        if (center.equals(TradeCenter.huobi.name()))
            return object.getBigDecimal("net_asset");
        else if (center.equals(TradeCenter.okcoin.name())) {
            return object.getJSONObject("info").getJSONObject("funds").getJSONObject("asset").getBigDecimal("net");
        }
        return null;
    }

    public UserInfo getUserInfo(String ret, String center) {
        if (StringUtil.isEmpty(ret) || StringUtil.isEmpty(center))
            return null;
        UserInfo userInfo = new UserInfo();
        JSONObject object = JSON.parseObject(ret);
        if (center.equals(TradeCenter.huobi.name())) {
            userInfo.setFreeCny(object.getBigDecimal("available_cny_display"));
            userInfo.setFreeLTC(object.getBigDecimal("available_ltc_display"));
            userInfo.setFreeBTC(object.getBigDecimal("available_btc_display"));
            return userInfo;
        } else if (center.equals(TradeCenter.okcoin.name())) {
            object = object.getJSONObject("info").getJSONObject("funds").getJSONObject("free");
            userInfo.setFreeBTC(object.getBigDecimal("btc"));
            userInfo.setFreeCny(object.getBigDecimal("cny"));
            userInfo.setFreeLTC(object.getBigDecimal("ltc"));
            return userInfo;
        }
        return null;
    }
}
