package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenter;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tonyqi on 17-1-4.
 * 深度数据获取
 */
@Component
public class DepthDataSource {
    @Autowired
    private HuobiService huobiApi;

    private Log log = LogFactory.getLog(DepthDataSource.class);

    /**
     * 深度数据源
     * <p>
     * sellCenter : 套利卖方交易所的名称
     * buyCenter : 套利买方交易所名称
     * priceMargin : 套利卖方与买方可套利差价
     * sellPrice : 卖方价格
     * buyPrice : 买方价格
     * sellAmount : 套利卖方深度数量
     * buyAmount : 套利买方深度数量
     * boolean canOrder : 有套利空间
     * boolean canReverseAmount : 可迁移头寸
     * reverseSellCenter : 卖方交易所名称
     * reverseBuyCenter : 接受头寸迁移的交易所名称
     * reverseSellPrice : 卖方价格
     * reverseBuyPrice : 买方价格
     * reverseSellAmount : 迁移卖方深度数量
     * reverseBuyAmount : 迁移买方深度数量
     * <p>
     * 说明:如果行情不足以套利或者持币数量无法套利，才会考虑迁移
     * （迁移是因为有些交易所大多时间持续比另一个交易所价格高，偶尔价格持平）
     */
    private Map<String, Object> getTradeCenterDepth() {
        Map<String, Object> map = new HashMap<>();
        map.put("canReverseAmount", false);
        map.put("canOrder", false);
        Map<String, BigDecimal[]> huobiDepth = huobiApi.depth();//获取深度
        if (huobiDepth == null) {
            log.error("深度获取失败");
            return null;
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

        BigDecimal hbSellPM = hbBidPrice.subtract(okAskPrice);//火币买方价格 - ok卖方价格
        BigDecimal okSellPM = okBidPrice.subtract(hbAskPrice);//ok买方价格 - 火币卖方价格

        String targetCenter;
        if (!StringUtil.isEmpty(targetCenter = isReverse())) {
            BigDecimal hbS = hbSellPM.abs();
            BigDecimal okS = okSellPM.abs();
            /**满足迁移条件**/
            if (targetCenter.equals(TradeCenter.okcoin.name()) && hbS.compareTo(AllocationSource.canReversePriceM) <= 0)
                packageReverse(map, TradeCenter.huobi.name(), TradeCenter.okcoin.name(), hbBidPrice, okAskPrice, hbBidAmount, okAskAmount);
            else if (targetCenter.equals(TradeCenter.huobi.name()) && okS.compareTo(AllocationSource.canReversePriceM) <= 0)
                packageReverse(map, TradeCenter.okcoin.name(), TradeCenter.huobi.name(), okBidPrice, hbAskPrice, okBidAmount, hbAskAmount);
        }
        /**火币sell - ok buy 》= 盈利价差**/
        if (hbSellPM.compareTo(AllocationSource.price_margin) >= 0)
            packageGainsPriceMargin(map, TradeCenter.huobi.name(), TradeCenter.okcoin.name(), hbSellPM, hbBidPrice, okAskPrice, hbBidAmount, okAskAmount);
        /**ok sell - 火币 buy 》= 盈利价差**/
        else if (okSellPM.compareTo(AllocationSource.price_margin) >= 0)
            packageGainsPriceMargin(map, TradeCenter.okcoin.name(), TradeCenter.huobi.name(), okSellPM, okBidPrice, hbAskPrice, okBidAmount, hbAskAmount);
        return map;
    }

    private void packageReverse(Map<String, Object> map,
                                String reverseSellCenter, String reverseBuyCenter,
                                BigDecimal reverseSellPrice, BigDecimal reverseBuyPrice,
                                BigDecimal reverseSellAmount, BigDecimal reverseBuyAmount) {
        log.info("满足迁移头寸条件，迁移卖方交易所为：" + reverseSellCenter + ",接受迁移头寸交易所为:" + reverseBuyCenter + ",卖价为:" + reverseSellPrice + ",买价为:" + reverseBuyPrice);
        map.put("canReverseAmount", true);
        map.put("reverseSellCenter", reverseSellCenter);
        map.put("reverseBuyCenter", reverseBuyCenter);
        map.put("reverseSellPrice", reverseSellPrice);
        map.put("reverseBuyPrice", reverseBuyPrice);
        map.put("reverseSellAmount", reverseSellAmount);
        map.put("reverseBuyAmount", reverseBuyAmount);
    }

    private void packageGainsPriceMargin(Map<String, Object> map, String sellCenter, String buyCenter,
                                         BigDecimal priceMargin, BigDecimal sellPrice, BigDecimal buyPrice,
                                         BigDecimal sellAmount, BigDecimal buyAmount) {
        map.put("canOrder", true);
        map.put("sellCenter", sellCenter);
        map.put("buyCenter", buyCenter);
        map.put("priceMargin", priceMargin);
        map.put("sellPrice", sellPrice);
        map.put("buyPrice", buyPrice);
        map.put("sellAmount", sellAmount);
        map.put("buyAmount", buyAmount);
    }

    /*查看库中是否需要迁移*/
    public String isReverse() {
        boolean isReverse = false;//// TODO: 17-1-4 从库中查询是否需要迁移头寸
        String center = "";//并获取迁移至哪个交易所
        return null;
    }
}
