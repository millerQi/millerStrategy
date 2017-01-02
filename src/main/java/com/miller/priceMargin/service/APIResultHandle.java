package com.miller.priceMargin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miller.priceMargin.model.order.OrderInfo;
import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.util.StringUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by Miller on 2017/1/1.
 */
@Component
public class APIResultHandle {

    public TradeInfo getTradeInfo(String result, String center) {
        if (StringUtil.isEmpty(center) || StringUtil.isEmpty(result))
            return null;
        JSONObject object = JSON.parseObject(result);
        if (center.equals("okcoin"))
            return new TradeInfo(object.getLong("order_id"), object.getString("result"));
        else if (center.equals("huobi"))
            return new TradeInfo(object.getLong("id"), object.getString("result"));
        return null;
    }

    public OrderInfo getOrderInfo(String result, String center) {
        if (StringUtil.isEmpty(result) || StringUtil.isEmpty(center))
            return null;
        JSONObject o = JSON.parseObject(result);
        OrderInfo orderInfo = new OrderInfo();
        if (center.equals("okcoin")) {
            orderInfo.setResult(o.getString("result"));
            o = o.getJSONArray("orders").getJSONObject(0);
            orderInfo.setAmount(o.getBigDecimal("amount"));
            orderInfo.setAvgPrice(o.getBigDecimal("avg_price"));
            orderInfo.setDealAmount(o.getBigDecimal("deal_amount"));
            orderInfo.setPrice(o.getBigDecimal("price"));
            orderInfo.setTradeDirection(o.getString("type"));
            return orderInfo;
        } else if (center.equals("huobi")) {
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
        if (center.equals("huobi"))
            return object.getBigDecimal("net_asset");
        else if (center.equals("okcoin")) {
            return object.getJSONObject("info").getJSONObject("funds").getJSONObject("asset").getBigDecimal("net");
        }
        return null;
    }
}
