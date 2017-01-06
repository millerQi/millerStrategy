package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenter;
import com.miller.priceMargin.model.order.UserInfo;
import com.miller.priceMargin.util.APIResultHandle;
import com.miller.priceMargin.service.PriceMarginService;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import com.miller.priceMargin.util.CommonUtil;
import com.miller.priceMargin.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by tonyqi on 17-1-4.
 * 多交易所套利，缓慢搬砖，适用于差价大周期长的差价交易所
 */
@Component
public class MoreCenterPriceMargin {

    @Autowired
    private DepthDataSource depthDataSource;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private HuobiService huobiService;

    @Autowired
    private OkcoinService okcoinService;

    @Autowired
    private APIResultHandle apiResultHandle;

    @Autowired
    private PriceMarginService priceMarginService;

    private Log log = LogFactory.getLog(MoreCenterPriceMargin.class);

    /*上次下单差价*/
    private BigDecimal lastPriceMargin;
    /*上次卖方交易所*/
    private String lastSellCenter;
    /*价差上升百分比系数*/
    private BigDecimal percent = BigDecimal.valueOf(1.1);


    public void startStrategy() {
        initAccount();
        while (AllocationSource.strategyOpen) {
            CommonUtil.sleep(1000);
            /**获取深度**/
            Map<String, Object> map = depthDataSource.getTradeCenterDepth();
            if (map == null)
                continue;
            boolean orderComplete = false;
            /**套利方法**/
            if ((boolean) map.get("canOrder"))
                orderComplete = orderMethod(map);
            /**迁移头寸方法**/
            if (!orderComplete && (boolean) map.get("canReverseAmount"))
                reverseAmountMethod(map);
        }
    }

    private void initAccount() {
        String okcoinRet = okcoinService.userinfo();
        String hbRet = huobiService.getAccountInfo();
        UserInfo okUserInfo = apiResultHandle.getUserInfo(okcoinRet, TradeCenter.okcoin.name());
        UserInfo hbUserInfo = apiResultHandle.getUserInfo(hbRet, TradeCenter.huobi.name());
        AllocationSource.okFreePrice = okUserInfo.getFreeCny();
        AllocationSource.okFreeBTCAmount = okUserInfo.getFreeBTC();
        AllocationSource.okFreeLTCAmount = okUserInfo.getFreeLTC();

        AllocationSource.hbFreePrice = hbUserInfo.getFreeCny();
        AllocationSource.hbFreeBTCAmount = hbUserInfo.getFreeBTC();
        AllocationSource.hbFreeLTCAmount = hbUserInfo.getFreeLTC();

        AllocationSource.startTime = System.currentTimeMillis();

        BigDecimal okNetAsset = apiResultHandle.getNetAsset(okcoinRet, "okcoin");
        BigDecimal hbNetAsset = apiResultHandle.getNetAsset(hbRet, "huobi");
        priceMarginService.updateFreePrice(okNetAsset, hbNetAsset);
        priceMarginService.updateLastPrice(okNetAsset, hbNetAsset);
    }

    private boolean reverseAmountMethod(Map<String, Object> map) {
        String reverseSellCenter = StringUtil.getString(map.get("reverseSellCenter"));
        String reverseBuyCenter = StringUtil.getString(map.get("reverseBuyCenter"));
        BigDecimal reverseBuyPrice = CommonUtil.getDecimalMuchBigPrice((BigDecimal) map.get("reverseBuyPrice"));
        BigDecimal reverseSellPrice = CommonUtil.getDecimalMuchSmallPrice((BigDecimal) map.get("reverseSellPrice"));
        BigDecimal reverseSellAmount = (BigDecimal) map.get("reverseSellAmount");
        BigDecimal reverseBuyAmount = (BigDecimal) map.get("reverseBuyAmount");

        /*迁移头寸是套利下单头寸的2倍*/
        BigDecimal tickerAmount = AllocationSource.tickAmount.multiply(BigDecimal.valueOf(1.5));
        /**check depth amount**/
        if (tickerAmount.compareTo(reverseBuyAmount) == 1
                || tickerAmount.compareTo(reverseSellAmount) == 1)
            return false;
        /**check freeAmount and freePrice**/
        if (!validateFree(reverseSellCenter, reverseBuyCenter, reverseBuyPrice, tickerAmount))
            return false;

        log.info("start reverse , tick_amount : " + tickerAmount + " target_reverse_center : " + reverseBuyCenter);
        /**trade**/
        tradeService.trade(reverseSellCenter, reverseBuyCenter, reverseSellPrice, reverseBuyPrice, tickerAmount, tickerAmount, AllocationSource.coin, true);
        return true;
    }

    /**
     * 下单，下单完成则返回true,否则返回false
     *
     * @param map
     * @return
     */
    private boolean orderMethod(Map<String, Object> map) {
        String sellCenter = StringUtil.getString(map.get("sellCenter"));
        String buyCenter = StringUtil.getString(map.get("buyCenter"));
        BigDecimal priceMargin = (BigDecimal) map.get("priceMargin");
        BigDecimal sellAmount = (BigDecimal) map.get("sellAmount");
        BigDecimal buyAmount = (BigDecimal) map.get("buyAmount");
        BigDecimal buyPrice = CommonUtil.getDecimalMuchBigPrice((BigDecimal) map.get("buyPrice"));
        BigDecimal sellPrice = CommonUtil.getDecimalMuchSmallPrice((BigDecimal) map.get("sellPrice"));
        /**判断是否持续上次的搬砖**/
        if (!StringUtil.isEmpty(lastSellCenter) && lastSellCenter.equals(sellCenter)) {
            if (priceMargin.compareTo(lastPriceMargin.multiply(percent).setScale(2, BigDecimal.ROUND_DOWN)) == -1)
                return false;
        } else {//初始化
            lastSellCenter = sellCenter;
            lastPriceMargin = priceMargin;
        }
        /**check depth amount**/
        if (AllocationSource.tickAmount.compareTo(buyAmount) == 1
                || AllocationSource.tickAmount.compareTo(sellAmount) == 1)
            return false;

        /**check freeAmount and freePrice**/
        if (!validateFree(sellCenter, buyCenter, buyPrice, AllocationSource.tickAmount))
            return false;

        /**trade**/
        boolean tradeSuccess = tradeService.trade(sellCenter, buyCenter, sellPrice, buyPrice, AllocationSource.tickAmount, AllocationSource.tickAmount, AllocationSource.coin, false);
        if (tradeSuccess)
            lastPriceMargin = lastPriceMargin.multiply(percent);
        return tradeSuccess;
    }

    private boolean validateFree(String sellCenter, String buyCenter, BigDecimal buyPrice, BigDecimal tickerAmount) {
        /**check free_amount**/
        if (tickerAmount.compareTo(AllocationSource.getFreeAmount(sellCenter)) == 1)
            return false;
        /**check free_price**/
        return buyPrice.multiply(tickerAmount).compareTo(AllocationSource.getFreePrice(buyCenter)) == -1;
    }
}
