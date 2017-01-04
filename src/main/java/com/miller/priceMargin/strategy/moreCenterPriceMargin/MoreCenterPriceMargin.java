package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenter;
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

    private Log log = LogFactory.getLog(MoreCenterPriceMargin.class);

    /*持续套利*/
    private boolean continued = false;
    /*上次下单差价*/
    private BigDecimal lastPriceMargin;
    /*上次卖方交易所*/
    private String lastSellCenter;
    /*价差上升百分比系数*/
    private BigDecimal pencent = BigDecimal.valueOf(1.2);


    public void startStrategy() {
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

    private void reverseAmountMethod(Map<String, Object> map) {

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
        if (continued && lastSellCenter.equals(sellCenter)) {
            if (priceMargin.compareTo(lastPriceMargin = lastPriceMargin.multiply(pencent)) == -1)
                return false;
        } else {//初始化
            continued = true;
            lastSellCenter = sellCenter;
            lastPriceMargin = priceMargin;
        }

        if (sellAmount.compareTo(AllocationSource.tickAmount) < 0
                || buyAmount.compareTo(AllocationSource.tickAmount) < 0) {
            log.warn("can order but amount is'n enough ! sell_center : " + sellCenter + " tick_amount : "
                    + AllocationSource.tickAmount + " sell_amount : " + sellAmount + " buy_amount : " + buyAmount);
            return false;
        }

        /**check freeAmount and freePrice**/
        if (sellCenter.equals(TradeCenter.okcoin.name())) {
            /**check okcoin_free_amount**/
            if (AllocationSource.tickAmount.compareTo(AllocationSource.okFreeAmount) == 1) {
                log.warn("can order but okcoin's free_amount is'n enough ! tick_amount : "
                        + AllocationSource.tickAmount + " ok_free_amount = " + AllocationSource.okFreeAmount);
                return false;
            }
            /**check huobi_free_price**/
            BigDecimal totalPrice;
            if (AllocationSource.hbFreePrice.compareTo(totalPrice = buyPrice.multiply(AllocationSource.tickAmount)) == -1) {
                log.warn("can order but huobi's free_price is'n enough !  total_price : "
                        + totalPrice + " huobi's free_price : " + AllocationSource.hbFreePrice);
                return false;
            }
        } else {//huobi
            /**check huobi_free_amount**/
            if (AllocationSource.tickAmount.compareTo(AllocationSource.hbFreeAmount) == 1) {
                log.warn("can order but huobi's free_amount is'n enough ! tick_amount : "
                        + AllocationSource.tickAmount + " ok_free_amount = " + AllocationSource.hbFreeAmount);
                return false;
            }
            /**check ok_free_price**/
            BigDecimal totalPrice;
            if (AllocationSource.okFreePrice.compareTo(totalPrice = buyPrice.multiply(AllocationSource.tickAmount)) == -1) {
                log.warn("can order but okcoin's free_price is'n enough !  total_price : "
                        + totalPrice + " okcoin's free_price : " + AllocationSource.hbFreePrice);
                return false;
            }
        }
        // TODO: 17-1-4 下单
        return true;
    }
}
