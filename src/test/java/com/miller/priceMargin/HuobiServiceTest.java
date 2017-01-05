package com.miller.priceMargin;

import com.miller.priceMargin.model.order.UserInfo;
import com.miller.priceMargin.service.APIResultHandle;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
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
    @Autowired
    private OkcoinService okcoinService;

    @Test
    public void testTrade() {
        UserInfo userInfo = apiResultHandle.getUserInfo(okcoinService.userinfo(), "okcoin");
        System.out.println(userInfo);
    }
}

