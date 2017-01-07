package com.miller.priceMargin;

import com.miller.priceMargin.model.moreCenterPriceMargin.SystemAllocation;
import com.miller.priceMargin.model.order.UserInfo;
import com.miller.priceMargin.service.SystemAllocationService;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import com.miller.priceMargin.util.APIResultHandle;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Miller on 2017/1/7.
 */
public class InterfaceServiceTest extends BaseTest {
    @Autowired
    private SystemAllocationService systemAllocationService;
    @Autowired
    private APIResultHandle apiResultHandle;
    @Autowired
    private OkcoinService okcoinService;
    @Autowired
    private HuobiService huobiService;

    @Test
    public void testSystemAllocation() {
        SystemAllocation systemAllocation = systemAllocationService.getSystemAllocation();
        System.out.println(systemAllocation);
    }

    @Test
    public void testResult() {
        UserInfo okUserInfo = apiResultHandle.getUserInfo(okcoinService.userinfo(), "okcoin");
        UserInfo hbUserInfo = apiResultHandle.getUserInfo(huobiService.getAccountInfo(), "huobi");
        System.out.println(okUserInfo);
        System.out.println(hbUserInfo);
    }
}
