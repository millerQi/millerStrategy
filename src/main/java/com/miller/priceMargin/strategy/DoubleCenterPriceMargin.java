package com.miller.priceMargin.strategy;

import com.miller.priceMargin.Access;
import com.miller.priceMargin.model.Order;
import com.miller.priceMargin.model.OrderGain;
import com.miller.priceMargin.model.order.OrderInfo;
import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.service.APIResultHandle;
import com.miller.priceMargin.service.PriceMarginService;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Miller on 2017/1/1.
 */
@Component
public class DoubleCenterPriceMargin {

    @Autowired
    private HuobiService huobiApi;
    @Autowired
    private OkcoinService okcoinService;
    @Autowired
    private APIResultHandle resultHandle;
    @Autowired
    private PriceMarginService priceMarginService;

    public static BigDecimal priceMargin = BigDecimal.valueOf(0.8);//程序启动时赋值

    public static BigDecimal tradeAmount = BigDecimal.valueOf(0.1);//程序启动后检测完账号持币情况后赋值

    private static Log log = LogFactory.getLog(DoubleCenterPriceMargin.class);

    private static Integer a = 1;

    private static Integer b = -1;

    private static BigDecimal gains = BigDecimal.ZERO;//总盈利

    /**
     * 搬砖套利
     */
    private void trade(BigDecimal okPrice, BigDecimal hbPrice, Integer hasCoin) {
        String amount = String.valueOf(tradeAmount);
        long okTID = 0, hbTID = 0;
        String hbP, okP;
        /**------okcoin卖 huobi买-------*/
        if (hasCoin.equals(a)) {//1-okcoin持币
            String result = huobiApi.buy(1, hbP = getMuchBigPrice(hbPrice), amount, null, "buy");//huobi通过rest接口下单
            TradeInfo huobiTrade = resultHandle.getTradeInfo(result, "huobi");
            if (huobiTrade.getResult().equals("false")) {
                log.error("程序异常!火币接口下单失败,方向buy,单价" + hbP + ",订单数量" + amount);
                System.exit(0);
            }
            hbTID = huobiTrade.getOrderId();
            log.info("huobi trade buy success");
            TradeInfo trade = resultHandle.getTradeInfo(okcoinService.trade("btc_cny", "sell", okP = getMuchSmallPrice(okPrice), amount), "okcoin");//okcoin通过rest下单
            if (trade == null) {
                log.error("程序异常!OKCOIN接口下单失败,方向sell,单价" + okP + ",订单数量" + amount);
                System.exit(0);
            }
            priceMarginService.updateHasCoin(b);
            okTID = trade.getOrderId();
            log.info("okcoin trade sell success");
        }
        /**------okcoin买 huobi卖-------*/
        else if (hasCoin.equals(b)) {//-1huobi持币
            TradeInfo huobiTrade = resultHandle.getTradeInfo(huobiApi.sell(1, hbP = getMuchSmallPrice(hbPrice), amount, null, "sell"), "huobi");
            if (huobiTrade.getResult().equals("false")) {
                log.error("程序异常!火币接口下单失败,方向sell,单价" + hbP + ",订单数量" + amount);
                System.exit(0);
            }
            log.info("huobi trade sell success");
            hbTID = huobiTrade.getOrderId();
            TradeInfo trade = resultHandle.getTradeInfo(okcoinService.trade("btc_cny", "buy", okP = getMuchBigPrice(okPrice), amount), "okcoin");
            if (trade == null) {
                log.error("程序异常!OKCOIN接口下单失败,方向buy,单价" + okP + ",订单数量" + amount);
                System.exit(0);
            }
            log.info("okcoin trade buy success");
            priceMarginService.updateHasCoin(a);
            okTID = trade.getOrderId();
        }
        reckonGains(okTID, hbTID, okPrice, hbPrice, hasCoin);
    }

    public void checkDepthAndStartTrade() {
        Integer hasCoin = priceMarginService.getHasCoin();
        Map<String, BigDecimal[]> huobiDepth = huobiApi.depth();//获取深度
        if (huobiDepth == null) {
            log.error("深度获取失败");
            return;
        }
        BigDecimal[] hbAsks = huobiDepth.get("hbAsks");
        BigDecimal[] hbBids = huobiDepth.get("hbBids");
        BigDecimal[] okAsks = huobiDepth.get("okAsks");
        BigDecimal[] okBids = huobiDepth.get("okBids");

        BigDecimal okBidAmount = okBids[1];//okcoin买方深度数量
        BigDecimal hbAskAmount = hbAsks[1];//huobi卖方深度数量

        BigDecimal okBidPrice = okBids[0];//okcoin买方价格
        BigDecimal hbAskPrice = hbAsks[0];//huobi卖方价格

        BigDecimal hbBidAmount = hbBids[1];//huobi买方深度数量
        BigDecimal okAskAmount = okAsks[1];//ok卖方深度数量

        BigDecimal hbBidPrice = hbBids[0];//huobi买方价格
        BigDecimal okAskPrice = okAsks[0];//okcoin卖方价格

        if (hasCoin.equals(a)) {//ok持币
            if ((okBidPrice.subtract(hbAskPrice)).compareTo(priceMargin) >= 0) {//ok的买方价格减去火币卖方价格大于等于价差则可以开单
                if (okBidAmount.compareTo(tradeAmount) >= 0 && hbAskAmount.compareTo(tradeAmount) >= 0) {//判断数量
                    trade(okBidPrice, hbAskPrice, hasCoin);
                } else {
                    log.info("深度价格足够，币数量不足,ok买方持币数量：" + okBidAmount + "，huobi卖方持币数量:" + hbAskAmount);
                }
            } else {
                long now = System.currentTimeMillis();
                if (now - pong >= pongTime) {
                    log.warn("okcoin has coin pong!okcoin depth price " + okBidPrice + ",huobi depth price " + hbAskPrice);
                    pong = now;
                }
            }
        } else {//火币持币
            if ((hbBidPrice.subtract(okAskPrice)).compareTo(priceMargin) >= 0) {//火币的买方价格减去ok卖方价格大于等于差价则可以开单
                if (hbBidAmount.compareTo(tradeAmount) >= 0 && okAskAmount.compareTo(tradeAmount) >= 0) {//判断数量
                    trade(okAskPrice, hbBidPrice, hasCoin);
                } else {
                    log.info("深度价格足够，币数量不足,hb买方持币数量：" + okBidAmount + "，ok卖方持币数量:" + hbAskAmount);
                }
            } else {
                long now = System.currentTimeMillis();
                if (now - pong >= pongTime) {
                    log.warn("huobi has coin pong!");
                    pong = now;
                }
            }
        }
    }

    private long pong = System.currentTimeMillis();//心跳
    private long pongTime = 300000;

    private void reckonGains(long okTID, long hbTID, BigDecimal okPrice, BigDecimal hbPrice, Integer hasCoin) {
        int count = 0;
        count++;
        sleep(300);

        String ret = huobiApi.getOrderInfo(1, hbTID, "order_info");
        OrderInfo hbOrderInfo = resultHandle.getOrderInfo(ret, "huobi");
        OrderInfo okOrderInfo = resultHandle.getOrderInfo(okcoinService.order_info("btc_cny", okTID), "okcoin");
        updateLastPrice();//修改最新净资产
        /**订单可能未成交，或者数据没同步 递归调用**/
        BigDecimal hbDeal = hbOrderInfo.getDealAmount();
        BigDecimal okDeal = okOrderInfo.getDealAmount();
        if (hbDeal.compareTo(BigDecimal.ZERO) == 0
                || okDeal.compareTo(BigDecimal.ZERO) == 0
                || hbDeal.compareTo(hbOrderInfo.getAmount()) == -1
                || okDeal.compareTo(okOrderInfo.getAmount()) == -1) {
            if (count > 20) {
                log.error("订单详情调用失败或没有完全成交，数据库订单少记录一笔！hbTid = " + hbTID + ",okTid = " + okTID);
                return;
            } else
                reckonGains(okTID, hbTID, okPrice, hbPrice, hasCoin);
        }
        BigDecimal okAvgPrice = okOrderInfo.getAvgPrice();
        BigDecimal hbAvgPrice = hbOrderInfo.getAvgPrice();
        BigDecimal expectGains, realGains;
        if (hasCoin.equals(a)) {
            expectGains = tradeAmount.multiply(okPrice.subtract(hbPrice));
            realGains = tradeAmount.multiply(okAvgPrice.subtract(hbAvgPrice));
        } else {
            expectGains = tradeAmount.multiply(hbPrice.subtract(okPrice));
            realGains = tradeAmount.multiply(hbAvgPrice.subtract(okAvgPrice));
        }
        Long hbId = saveOrder(hbOrderInfo, "huobi", hbPrice);
        Long okId = saveOrder(okOrderInfo, "okcoin", okPrice);


        saveOrderGain(hbId, okId, realGains, hasCoin);
        log.warn("搬砖完成--起始价格--okcoin:" + okPrice + ",huobi:" + hbPrice + "--实际成交价--okcoin:" + okAvgPrice + ",huobi:" + hbAvgPrice);
        BigDecimal huadian = expectGains.subtract(realGains);
        if (huadian.compareTo(BigDecimal.ZERO) == -1)
            huadian = huadian.subtract(huadian.multiply(BigDecimal.valueOf(2)));
        log.warn("==========预计盈利:" + expectGains + " , 实际盈利:" + realGains + " ，滑点" + huadian + "==========");
        priceMarginService.addGains(realGains);
    }

    private void updateLastPrice() {
        BigDecimal okNetAsset = resultHandle.getNetAsset(okcoinService.userinfo(), "okcoin");
        BigDecimal hbNetAsset = resultHandle.getNetAsset(huobiApi.getAccountInfo(), "huobi");
        priceMarginService.updateLastPrice(okNetAsset, hbNetAsset);
    }

    private void saveOrderGain(Long hbId, Long okId, BigDecimal realGains, Integer hasCoin) {
        OrderGain orderGain = new OrderGain();
        Long buyId, sellId;
        if (hasCoin.equals(a)) {//okcoin为卖
            sellId = okId;
            buyId = hbId;
        } else {
            sellId = hbId;
            buyId = okId;
        }
        orderGain.setSellOrderId(sellId);
        orderGain.setBuyOrderId(buyId);
        orderGain.setGains(realGains);
        priceMarginService.saveOrderGain(orderGain);
    }

    private Long saveOrder(OrderInfo orderInfo, String center, BigDecimal price) {
        Order order = new Order();
        order.setTickerPrice(price);
        order.setAmount(orderInfo.getAmount());
        order.setTradeDirection(orderInfo.getTradeDirection());
        order.setTradeCenter(center);
        order.setDealAmount(orderInfo.getDealAmount());
        order.setDealPrice(orderInfo.getAvgPrice());
        order.setCreateTime(new Timestamp(new Date().getTime()));
        return priceMarginService.saveOrder(order);
    }

    private String getMuchSmallPrice(BigDecimal price) {
        return String.valueOf(price.multiply(BigDecimal.valueOf(0.95)).setScale(2, BigDecimal.ROUND_DOWN));
    }

    private String getMuchBigPrice(BigDecimal price) {
        return String.valueOf(price.multiply(BigDecimal.valueOf(1.05)).setScale(2, BigDecimal.ROUND_DOWN));
    }

    public void initData() {//修改初始数据
        BigDecimal okNetAsset = resultHandle.getNetAsset(okcoinService.userinfo(), "okcoin");
        BigDecimal hbNetAsset = resultHandle.getNetAsset(huobiApi.getAccountInfo(), "huobi");
        priceMarginService.updateFreePrice(okNetAsset, hbNetAsset);
        priceMarginService.updateLastPrice(okNetAsset, hbNetAsset);
    }

    private static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
