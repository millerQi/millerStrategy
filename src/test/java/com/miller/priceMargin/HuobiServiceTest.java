package com.miller.priceMargin;

import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.service.APIResultHandle;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Miller on 2017/1/2.
 */
public class HuobiServiceTest extends BaseTest {
    @Autowired
    private HuobiService huobiService;
    @Autowired
    private APIResultHandle apiResultHandle;

    @Test
    public void testTrade() {
//        TradeInfo tradeInfo = apiResultHandle.getTradeInfo(huobiService.buy(1, "6900", "0.01", null, "buy"), "huobi");
//        System.out.println(tradeInfo);
    }
}

