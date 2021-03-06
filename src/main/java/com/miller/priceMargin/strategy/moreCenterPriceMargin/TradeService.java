package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenterEnum;
import com.miller.priceMargin.model.moreCenterPriceMargin.OppositeOrder;
import com.miller.priceMargin.model.order.OrderInfo;
import com.miller.priceMargin.model.order.TradeInfo;
import com.miller.priceMargin.model.order.UserInfo;
import com.miller.priceMargin.service.OppositeOrderService;
import com.miller.priceMargin.service.SystemStatusService;
import com.miller.priceMargin.service.TradeCenterService;
import com.miller.priceMargin.util.APIResultHandle;
import com.miller.priceMargin.tradeCenter.CommonTradeService;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import com.miller.priceMargin.util.CommonUtil;
import com.miller.priceMargin.weChat.WeChatSendMessage;
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
    private TradeCenterService tradeCenterService;
    @Autowired
    private OppositeOrderService oppositeOrderService;
    @Autowired
    private SystemStatusService systemStatusService;

    private Log log = LogFactory.getLog(TradeService.class);

    private ExecutorService pool = Executors.newSingleThreadExecutor();

    boolean trade(String sellCenter, String buyCenter, BigDecimal sellPrice, BigDecimal buyPrice,
                  BigDecimal sellAmount, BigDecimal buyAmount, int coin, boolean isReverse) {
        TradeInfo sellInfo = commonTradeService.trade(sellCenter, sellPrice.setScale(2, BigDecimal.ROUND_DOWN), sellAmount = sellAmount.setScale(2, BigDecimal.ROUND_DOWN), "sell", coin);
        if (sellInfo == null) {
            log.error("sell order failed! sell_center :" + sellCenter + " , sell_amount " + sellAmount
                    + " , buy_center :" + buyCenter + " ,buy_amount :" + buyAmount);
            return false;
        }
        TradeInfo buyInfo = commonTradeService.trade(buyCenter, buyPrice.setScale(2, BigDecimal.ROUND_DOWN), buyAmount = buyAmount.setScale(2, BigDecimal.ROUND_DOWN), "buy", coin);
        if (buyInfo == null) {
            //平仓
            closeout(false, sellAmount, sellInfo.getOrderId());
            log.error("sell order complete , but buy order failed ，start closeout , sell_center :" + sellCenter + " , sell_amount " + sellAmount
                    + " , buy_center :" + buyCenter + " ,buy_amount :" + buyAmount);
            return false;
        }
        pool.execute(() -> reckonGains(sellInfo.getOrderId(), sellCenter, buyInfo.getOrderId(), buyCenter, coin));
        return true;
    }

    private void closeout(boolean closeoutBuy, BigDecimal amount, Long TID) {//// TODO: 2017/1/7 平仓完毕，调用reckonGains
//        String direction = "buy";
//        if (closeoutBuy)
//            direction = "sell";
        WeChatSendMessage.sendMsg("sell success , buy error , system exit!");
        System.exit(0);
    }

    private void reckonGains(long sellTID, String sellCenter, long buyTID, String buyCenter, int coin) {
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
                    updateLastPrice();//修改最新净资产
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
                    updateLastPrice();//修改最新净资产
                    return;
                }
            } else
                flag = false;
        }
        BigDecimal sellAvgPrice = sellOrderInfo.getAvgPrice();
        BigDecimal buyAvgPrice = buyOrderInfo.getAvgPrice();
        BigDecimal dealGains = (sellAvgPrice.subtract(buyAvgPrice)).multiply(buyOrderInfo.getDealAmount());
        BigDecimal sellAmount = sellOrderInfo.getAmount();
        updateLastPrice(sellOrderInfo.getDealAmount(), buyOrderInfo.getDealAmount(), sellCenter, buyCenter, sellAvgPrice, buyAvgPrice);//修改最新净资产
        OppositeOrder oppositeOrder = new OppositeOrder();
        oppositeOrder.setGains(dealGains);
        oppositeOrder.setSellCenter(sellCenter);
        oppositeOrder.setSellAmount(sellAmount);
        oppositeOrder.setSellAvgPrice(sellAvgPrice);
        oppositeOrder.setBuyCenter(buyCenter);
        oppositeOrder.setBuyAmount(buyOrderInfo.getAmount());
        oppositeOrder.setBuyAvgPrice(buyAvgPrice);
        oppositeOrderService.saveOppositeOrder(oppositeOrder);
        int ret = systemStatusService.updateGains(dealGains, sellAmount);
        if (ret != 1)
            log.error("gains 修改失败!dealGains:" + dealGains + ",sellAmount:" + sellAmount);
        String msg = "Trade complete !\n"
                + "sell_center : " + sellCenter + "\n"
                + "buy_center : " + buyCenter + "\n"
                + "trade_amount : " + sellAmount + "\n"
                + "buy_avg_price : " + buyAvgPrice + "\n"
                + "sell_avg_price : " + sellAvgPrice + "\n"
                + "gains : " + dealGains + "\n";
        WeChatSendMessage.sendMsg(msg);
        log.info("trade complete ! | trade_amount | " + sellAmount + " | " + "buy_avg_price | " + buyAvgPrice + " | sell_avg_price | " + sellAvgPrice + " | gains | " + dealGains + " |");
    }

    private void updateLastPrice() {
        UserInfo okUserInfo = resultHandle.getUserInfo(okcoinService.userinfo(), TradeCenterEnum.okcoin.name());
        UserInfo hbUserInfo = resultHandle.getUserInfo(huobiApi.getAccountInfo(), TradeCenterEnum.huobi.name());
        int coin = AllocationSource.coin;
        BigDecimal okFreeAmount, hbFreeAmount;
        if (coin == 1) {
            okFreeAmount = okUserInfo.getFreeBTC();
            hbFreeAmount = hbUserInfo.getFreeBTC();
        } else {
            okFreeAmount = okUserInfo.getFreeLTC();
            hbFreeAmount = hbUserInfo.getFreeLTC();
        }
        tradeCenterService.updateAsset(TradeCenterEnum.okcoin.name(), okFreeAmount, okUserInfo.getFreeCny());
        tradeCenterService.updateAsset(TradeCenterEnum.huobi.name(), hbFreeAmount, hbUserInfo.getFreeCny());
    }

    private void updateLastPrice(BigDecimal sellAmount, BigDecimal buyAmount, String sellCenter, String buyCenter, BigDecimal sellAvgPrice, BigDecimal buyAvgPrice) {
        tradeCenterService.updateAmount(sellCenter, sellAmount.multiply(sellAvgPrice), sellAmount.subtract(sellAmount.multiply(BigDecimal.valueOf(2))));
        BigDecimal costPrice = buyAmount.multiply(buyAvgPrice);
        tradeCenterService.updateAmount(buyCenter, costPrice.subtract(costPrice.multiply(BigDecimal.valueOf(2))), buyAmount);
    }
}
