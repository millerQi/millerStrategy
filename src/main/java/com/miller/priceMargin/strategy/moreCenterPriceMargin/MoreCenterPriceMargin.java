package com.miller.priceMargin.strategy.moreCenterPriceMargin;

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

    private Log log = LogFactory.getLog(MoreCenterPriceMargin.class);

    /*持续套利*/
    private boolean continued = false;
    /*上次下单差价*/
    private BigDecimal lastPriceMargin;
    /*上次卖方交易所*/
    private String lastSellCenter;
    /*价差上升百分比系数*/
    private BigDecimal percent = BigDecimal.valueOf(1.2);


    public void startStrategy() {
        // TODO: 17-1-5
        while (AllocationSource.strategyOpen) {
            /**获取深度**/
            Map<String, Object> map = depthDataSource.getTradeCenterDepth();
            boolean orderComplete = false;
            /**套利方法**/
            if ((boolean) map.get("canOrder"))
                orderComplete = orderMethod(map);
            /**迁移头寸方法**/
            if (!orderComplete && (boolean) map.get("canReverseAmount"))
                reverseAmountMethod(map);
            CommonUtil.sleep(1000);
        }
    }

    private boolean reverseAmountMethod(Map<String, Object> map) {
        String reverseSellCenter = StringUtil.getString(map.get("reverseSellCenter"));
        String reverseBuyCenter = StringUtil.getString(map.get("reverseBuyCenter"));
        BigDecimal reverseBuyPrice = CommonUtil.getDecimalMuchBigPrice((BigDecimal) map.get("reverseBuyPrice"));
        BigDecimal reverseSellPrice = CommonUtil.getDecimalMuchSmallPrice((BigDecimal) map.get("reverseSellPrice"));
        BigDecimal reverseSellAmount = (BigDecimal) map.get("reverseSellAmount");
        BigDecimal reverseBuyAmount = (BigDecimal) map.get("reverseBuyAmount");
        /**check depth amount**/
        if (AllocationSource.tickAmount.compareTo(reverseBuyAmount) == 1
                || AllocationSource.tickAmount.compareTo(reverseSellAmount) == 1)
            return false;
        /**check freeAmount and freePrice**/
        if (!validateFree(reverseSellCenter, reverseBuyCenter, reverseBuyPrice))
            return false;
        /**trade**/
        tradeService.trade(reverseSellCenter, reverseBuyCenter, reverseSellPrice, reverseBuyPrice, reverseSellAmount, reverseBuyAmount, AllocationSource.coin, true);
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
        if (continued && !StringUtil.isEmpty(lastSellCenter) && lastSellCenter.equals(sellCenter)) {
            if (priceMargin.compareTo(lastPriceMargin.multiply(percent)) == -1)
                return false;
        } else {//初始化
            continued = true;
            lastSellCenter = sellCenter;
            lastPriceMargin = priceMargin;
        }
        /**check depth amount**/
        if (AllocationSource.tickAmount.compareTo(buyAmount) == 1
                || AllocationSource.tickAmount.compareTo(sellAmount) == 1)
            return false;

        /**check freeAmount and freePrice**/
        if (!validateFree(sellCenter, buyCenter, buyPrice))
            return false;

        /**trade**/
        boolean tradeSuccess = tradeService.trade(sellCenter, buyCenter, sellPrice, buyPrice, AllocationSource.tickAmount, AllocationSource.tickAmount, AllocationSource.coin, false);
        if (tradeSuccess)
            lastPriceMargin = lastPriceMargin.multiply(percent);
        return tradeSuccess;
    }

    private boolean validateFree(String sellCenter, String buyCenter, BigDecimal buyPrice) {
        /**check free_amount**/
        BigDecimal freeAmount;
        if (AllocationSource.tickAmount.compareTo(freeAmount = AllocationSource.getFreeAmount(sellCenter)) == 1) {
            log.warn("can order but " + sellCenter + "'s free_amount is'n enough ! tick_amount : "
                    + AllocationSource.tickAmount + " " + sellCenter + "_free_amount = " + freeAmount);
            return false;
        }
        /**check free_price**/
        BigDecimal totalPrice;
        BigDecimal freePrice;
        if ((freePrice = AllocationSource.getFreePrice(buyCenter)).compareTo(totalPrice = buyPrice.multiply(AllocationSource.tickAmount)) == -1) {
            log.warn("can order but " + buyCenter + "'s free_price is'n enough !  total_price : "
                    + totalPrice + " " + buyCenter + "'s free_price : " + freePrice);
            return false;
        }
        return true;
    }
}
