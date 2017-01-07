package com.miller.priceMargin;

import com.miller.priceMargin.util.APIResultHandle;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Miller on 2017/1/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/Application-context-test.xml")
public class BaseTest {
    @Autowired
    private APIResultHandle resultHandle;
    @Autowired
    private HuobiService huobiService;
    @Autowired
    private OkcoinService okcoinService;

    @Test
    public void testUserInfo() {
        System.out.println(resultHandle.getNetAsset(okcoinService.userinfo(), "okcoin"));
        System.out.println(resultHandle.getNetAsset(huobiService.getAccountInfo(), "huobi"));
    }
}
