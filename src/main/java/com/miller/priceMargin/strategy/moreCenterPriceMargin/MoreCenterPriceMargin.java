package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenterEnum;
import com.miller.priceMargin.model.moreCenterPriceMargin.SystemAllocation;
import com.miller.priceMargin.model.moreCenterPriceMargin.SystemStatus;
import com.miller.priceMargin.model.moreCenterPriceMargin.TradeCenter;
import com.miller.priceMargin.model.order.UserInfo;
import com.miller.priceMargin.service.SystemAllocationService;
import com.miller.priceMargin.service.SystemStatusService;
import com.miller.priceMargin.service.TradeCenterService;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import com.miller.priceMargin.util.APIResultHandle;
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
    private SystemAllocationService systemAllocationService;
    @Autowired
    private TradeCenterService tradeCenterService;
    @Autowired
    private SystemStatusService systemStatusService;

    private Log log = LogFactory.getLog(MoreCenterPriceMargin.class);

    /*价差上升百分比系数*/
    private BigDecimal percent = BigDecimal.valueOf(1.1);

    static SystemAllocation systemAllocation;

    public void startStrategy() {
        /**初始化系统配置**/
        initSystemAllocation();

        /**检查系统状态**/
        initSystemStatus();

        /**同步用户详情**/
        initAccount();
        while (true) {
            /**预处理**/
            pretreatment();
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

    private void pretreatment() {
        SystemAllocation systemAllocation = systemAllocationService.getSystemAllocation();
        if (MoreCenterPriceMargin.systemAllocation == null)
            MoreCenterPriceMargin.systemAllocation = systemAllocation;
        else if (!MoreCenterPriceMargin.systemAllocation.equals(systemAllocation)) {
            log.info("system allocation change ! reload again ! ");
            MoreCenterPriceMargin.systemAllocation = systemAllocation;
        }
        if (!systemAllocation.isStrategyOpen()) {
            log.warn("system exit by user!");
            System.exit(0);
        }
        AllocationSource.coin = systemAllocation.getCoin();
    }

    private void initSystemStatus() {
        if (systemStatusService.existData() == 0) {
            SystemStatus systemStatus = new SystemStatus();
            systemStatus.setAllGains(BigDecimal.ZERO);
            systemStatus.setCoinSellCount(BigDecimal.ZERO);
            systemStatus.setGainsOrderCount(0);
            systemStatus.setLossOrderCount(0);
            systemStatusService.save(systemStatus);
        }
        log.info("init system status complete");
    }

    private void initSystemAllocation() {
        if (systemAllocationService.getSystemAllocation() == null) {
            SystemAllocation systemAllocation = new SystemAllocation();
            systemAllocation.setCoin(2);
            systemAllocation.setPriceMargin(BigDecimal.valueOf(0.03));
            systemAllocation.setReversePriceMargin(BigDecimal.ZERO);
            systemAllocation.setTickAmount(BigDecimal.ONE);
            systemAllocation.setReverseMultipleAmount(1.2f);
            systemAllocation.setStrategyOpen(true);
            systemAllocationService.saveSystemAllocation(systemAllocation);
        }
        log.info("init system allocation complete");
    }

    private void initAccount() {
        UserInfo okUserInfo = apiResultHandle.getUserInfo(okcoinService.userinfo(), TradeCenterEnum.okcoin.name());
        UserInfo hbUserInfo = apiResultHandle.getUserInfo(huobiService.getAccountInfo(), TradeCenterEnum.huobi.name());
        SystemAllocation systemAllocation = systemAllocationService.getSystemAllocation();
        if (systemAllocation == null) {
            log.error("system error ! not found system allocation !");
            System.exit(0);
        }
        if (okUserInfo == null || hbUserInfo == null) {
            log.error("userInfo get error ,try start again!");
            CommonUtil.sleep(1000);
            initAccount();
        }
        int coin = systemAllocation.getCoin();
        BigDecimal okFreeAmount, okBorrowAmount, hbFreeAmount, hbBorrowAmount;
        if (coin == 1) {
            okFreeAmount = okUserInfo.getFreeBTC();
            okBorrowAmount = okUserInfo.getBorrowBTC();
            hbFreeAmount = hbUserInfo.getFreeBTC();
            hbBorrowAmount = hbUserInfo.getBorrowBTC();
        } else {
            okFreeAmount = okUserInfo.getFreeLTC();
            okBorrowAmount = okUserInfo.getBorrowLTC();
            hbFreeAmount = hbUserInfo.getFreeLTC();
            hbBorrowAmount = hbUserInfo.getBorrowLTC();
        }
        TradeCenter okCenter = new TradeCenter(TradeCenterEnum.okcoin.name(), okUserInfo.getNetAsset(), okUserInfo.getFreeCny(), okFreeAmount, okBorrowAmount, okUserInfo.getBorrowPrice());
        TradeCenter hbCenter = new TradeCenter(TradeCenterEnum.huobi.name(), hbUserInfo.getNetAsset(), hbUserInfo.getFreeCny(), hbFreeAmount, hbBorrowAmount, hbUserInfo.getBorrowPrice());
        tradeCenterService.truncateTable();
        tradeCenterService.saveTradeCenter(okCenter);
        tradeCenterService.saveTradeCenter(hbCenter);
        log.info("init account complete");
    }

    private boolean reverseAmountMethod(Map<String, Object> map) {
        String reverseSellCenter = StringUtil.getString(map.get("reverseSellCenter"));
        String reverseBuyCenter = StringUtil.getString(map.get("reverseBuyCenter"));
        BigDecimal reverseBuyPrice = CommonUtil.getDecimalMuchBigPrice((BigDecimal) map.get("reverseBuyPrice"));
        BigDecimal reverseSellPrice = CommonUtil.getDecimalMuchSmallPrice((BigDecimal) map.get("reverseSellPrice"));
        BigDecimal reverseSellAmount = (BigDecimal) map.get("reverseSellAmount");
        BigDecimal reverseBuyAmount = (BigDecimal) map.get("reverseBuyAmount");

        /*迁移头寸是套利下单头寸的2倍*/
        BigDecimal tickerAmount = systemAllocation.getTickAmount().multiply(BigDecimal.valueOf(systemAllocation.getReverseMultipleAmount())).setScale(2, BigDecimal.ROUND_DOWN);
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
     */
    private boolean orderMethod(Map<String, Object> map) {
        String sellCenter = StringUtil.getString(map.get("sellCenter"));
        String buyCenter = StringUtil.getString(map.get("buyCenter"));
        BigDecimal sellAmount = (BigDecimal) map.get("sellAmount");
        BigDecimal buyAmount = (BigDecimal) map.get("buyAmount");
        BigDecimal buyPrice = CommonUtil.getDecimalMuchBigPrice((BigDecimal) map.get("buyPrice"));
        BigDecimal sellPrice = CommonUtil.getDecimalMuchSmallPrice((BigDecimal) map.get("sellPrice"));
        BigDecimal tickAmount = systemAllocation.getTickAmount();
        /**check depth amount**/
        if (tickAmount.compareTo(buyAmount) == 1
                || tickAmount.compareTo(sellAmount) == 1) {
            log.warn("depth's amount is not enough!");
            return false;
        }
        /**check freeAmount and freePrice**/
        if (!validateFree(sellCenter, buyCenter, buyPrice, tickAmount))
            return false;
        /**trade**/
        return tradeService.trade(sellCenter, buyCenter, sellPrice, buyPrice, tickAmount, tickAmount, AllocationSource.coin, false);
    }

    private boolean validateFree(String sellCenter, String buyCenter, BigDecimal buyPrice, BigDecimal tickerAmount) {
        BigDecimal freeAmount = tradeCenterService.getFreeAmount(sellCenter);
        BigDecimal freePrice = tradeCenterService.getFreePrice(buyCenter);
        /**check free_amount**/
        if (tickerAmount.compareTo(freeAmount) == 1) {
//            log.warn("free amount is not enough , tick_amount :" + tickerAmount + ", free_amount:" + freeAmount);
            return false;
        }
        /**check free_price**/
        BigDecimal tick_price;
        if ((tick_price = buyPrice.multiply(tickerAmount)).compareTo(freePrice) == 1) {
//            log.info("free price is not enough , tick_price :" + tick_price + ", free_price:" + freePrice);
            return false;
        }
        return true;
    }
}
