package com.miller.priceMargin;

import com.miller.priceMargin.util.APIResultHandle;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Miller on 2017/1/2.
 */
public class OkocinServiceTest extends BaseTest {

    @Autowired
    private OkcoinService okcoinService;
    @Autowired
    private APIResultHandle apiResultHandle;

    @Test
    public void testTrade() {
//        System.out.println(apiResultHandle.getTradeInfo(okcoinService.trade("btc_cny", "buy", "6800", "0.01"), "okcoin"));
    }

    @Test
    public void testOrderInfo() {
//        System.out.println(apiResultHandle.getOrderInfo(okcoinService.order_info("btc_cny", 7669729613L),"okcoin"));
    }
}
