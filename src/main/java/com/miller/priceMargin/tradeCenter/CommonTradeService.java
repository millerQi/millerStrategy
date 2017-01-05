package com.miller.priceMargin.tradeCenter;

import com.miller.priceMargin.enumUtil.TradeCenter;
import com.miller.priceMargin.model.order.OrderInfo;
import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.service.APIResultHandle;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import com.miller.priceMargin.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-5.
 */
@Component
public class CommonTradeService {
    @Autowired
    private HuobiService huobiService;

    @Autowired
    private OkcoinService okcoinService;

    @Autowired
    private APIResultHandle apiResultHandle;

    public TradeInfo trade(String tradeCenter, BigDecimal price, BigDecimal amount, String direction, int coin) {
        if (StringUtil.isEmpty(tradeCenter))
            return new TradeInfo(null, "false");
        if (tradeCenter.equals(TradeCenter.huobi.name())) {
            String result;
            if (direction.equals("buy"))
                result = huobiService.buy(coin, String.valueOf(price), String.valueOf(amount), null, "buy");
            else
                result = huobiService.sell(coin, String.valueOf(price), String.valueOf(amount), null, "sell");
            return apiResultHandle.getTradeInfo(result, tradeCenter);
        } else if (tradeCenter.equals(TradeCenter.okcoin.name())) {
            return apiResultHandle.getTradeInfo(okcoinService.trade(getSymbol(coin), direction, String.valueOf(price), String.valueOf(amount)), tradeCenter);
        }
        return null;
    }

    private String getSymbol(int coin) {
        if (coin == 1)
            return "btc_cny";
        return "ltc_cny";
    }

    public OrderInfo orderInfo(String tradeCenter, Long tid, int coin) {
        if (tradeCenter.equals(TradeCenter.huobi.name()))
            return apiResultHandle.getOrderInfo(huobiService.getOrderInfo(coin, tid, "order_info"), tradeCenter);
        else if (tradeCenter.equals(TradeCenter.okcoin.name()))
            return apiResultHandle.getOrderInfo(okcoinService.order_info(getSymbol(coin), tid), tradeCenter);
        else
            return null;
    }
}
