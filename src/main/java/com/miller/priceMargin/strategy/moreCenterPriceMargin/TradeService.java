package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.model.order.OrderInfo;
import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.service.APIResultHandle;
import com.miller.priceMargin.service.PriceMarginService;
import com.miller.priceMargin.tradeCenter.CommonTradeService;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import com.miller.priceMargin.util.CommonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tonyqi on 17-1-4.
 */
@Component
public class TradeService {

    @Autowired
    private CommonTradeService commonTradeService;
    @Autowired
    private APIResultHandle resultHandle;
    @Autowired
    private HuobiService huobiApi;
    @Autowired
    private OkcoinService okcoinService;
    @Autowired
    private PriceMarginService priceMarginService;

    private Log log = LogFactory.getLog(TradeService.class);

    private ExecutorService pool = Executors.newSingleThreadExecutor();

    boolean trade(String sellCenter, String buyCenter, BigDecimal sellPrice, BigDecimal buyPrice,
                  BigDecimal sellAmount, BigDecimal buyAmount, int coin, boolean isReverse) {
        TradeInfo sellInfo = commonTradeService.trade(sellCenter, sellPrice, sellAmount, "sell", coin);
        if (sellInfo.getResult().equals("false")) {
            log.error("sell order failed! sell_center :" + sellCenter + " , sell_amount " + sellAmount
                    + " , buy_center :" + buyCenter + " ,buy_amount :" + buyAmount);
            return false;
        }
        AllocationSource.addFreeAmount(sellCenter, coin, sellAmount.subtract(BigDecimal.valueOf(2).multiply(sellAmount)));

        TradeInfo buyInfo = commonTradeService.trade(buyCenter, buyPrice, buyAmount, "buy", coin);
        if (buyInfo.getResult().equals("false")) {
            //// TODO: 17-1-5 平卖出成功部分
            log.error("sell order complete , but buy order failed , sell_center :" + sellCenter + " , sell_amount " + sellAmount
                    + " , buy_center :" + buyCenter + " ,buy_amount :" + buyAmount);
            return false;
        }
        AllocationSource.addFreeAmount(buyCenter, coin, buyAmount);
        pool.execute(() -> reckonGains(sellInfo.getOrderId(), sellCenter, buyInfo.getOrderId(), buyCenter, coin, isReverse));
        return true;
    }

    private void reckonGains(long sellTID, String sellCenter, long buyTID, String buyCenter, int coin, boolean isReverse) {
        boolean flag = true;
        int count = 0;
        OrderInfo sellOrderInfo = null;
        OrderInfo buyOrderInfo = null;
        while (flag) {
            CommonUtil.sleep(300);
            count++;
            sellOrderInfo = commonTradeService.orderInfo(sellCenter, sellTID, coin);
            buyOrderInfo = commonTradeService.orderInfo(buyCenter, buyTID, coin);
            if (sellOrderInfo == null || buyOrderInfo == null) {
                if (count > 20) {
                    log.error("订单详情调用失败，数据库订单少记录一笔！sellTID = " + sellTID + ",buyTID = " + buyTID);
                    updateLastPrice();//修改最新净资产 // TODO: 17-1-5需要修改统一
                    return;
                }
                continue;
            }
            /**订单可能未成交，或者数据没同步 循环调用**/
            BigDecimal sellDeal = sellOrderInfo.getDealAmount();
            BigDecimal buyDeal = buyOrderInfo.getDealAmount();
            if (sellDeal.compareTo(BigDecimal.ZERO) == 0
                    || buyDeal.compareTo(BigDecimal.ZERO) == 0
                    || sellDeal.compareTo(sellOrderInfo.getAmount()) == -1
                    || buyDeal.compareTo(buyOrderInfo.getAmount()) == -1) {
                if (count > 20) {
                    log.error("订单详情调用失败或没有完全成交，数据库订单少记录一笔！sellTID = " + sellTID + ",buyTID = " + buyTID);
                    updateLastPrice();//修改最新净资产 // TODO: 17-1-5需要修改统一
                    return;
                }
            } else
                flag = false;
        }
        updateLastPrice();//修改最新净资产 TODO: 17-1-5需要修改统一
        BigDecimal sellAvgPrice = sellOrderInfo.getAvgPrice();
        BigDecimal buyAvgPrice = buyOrderInfo.getAvgPrice();
        BigDecimal dealGains = sellAvgPrice.subtract(buyAvgPrice);
        String head = "";
        if (isReverse)
            head = "| Reverse order ! | ";
        // TODO: 17-1-5 记录订单
        priceMarginService.addGains(dealGains);
        String msg = head + "| sell_avg_price | " + sellAvgPrice + " | buy_avg_price | " + buyAvgPrice + " | amount | " + sellOrderInfo.getAmount() + " | gains | " + dealGains + " | all_gains | " + priceMarginService.getGains() + " |";
        if (dealGains.compareTo(BigDecimal.ZERO) >= 0)
            log.info(msg);
        else
            log.error(msg);
    }

    private void updateLastPrice() {
        BigDecimal okNetAsset = resultHandle.getNetAsset(okcoinService.userinfo(), "okcoin");
        BigDecimal hbNetAsset = resultHandle.getNetAsset(huobiApi.getAccountInfo(), "huobi");
        priceMarginService.updateLastPrice(okNetAsset, hbNetAsset);
    }
}
