package com.miller.priceMargin.strategy.moreCenterPriceMargin;

import com.miller.priceMargin.enumUtil.TradeCenter;
import com.miller.priceMargin.model.order.UserInfo;
import com.miller.priceMargin.util.APIResultHandle;
import com.miller.priceMargin.tradeCenter.huobi.HuobiService;
import com.miller.priceMargin.tradeCenter.okcoin.OkcoinService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-5.
 * 监控各账户持币情况，及时迁移
 */
@Component
public class AccountMonitor {
    @Autowired
    private OkcoinService okcoinService;
    @Autowired
    private HuobiService huobiService;
    @Autowired
    private APIResultHandle apiResultHandle;

    private Log log = LogFactory.getLog(AccountMonitor.class);

    //任意一边大于等于总和的40%就停止迁移
    private BigDecimal stopPercent = BigDecimal.valueOf(0.4);

    //任意一边小于总和的10%就开始迁移
    private BigDecimal startPercent = BigDecimal.valueOf(0.2);

    private int count = 0;

    @Scheduled(fixedRate = 60000)//一分钟执行一次
    public void startAccountMonitor() {
        if (count == 0) {
            log.info("start account monitor!");
            count++;
        }
        UserInfo okcoinUserInfo = apiResultHandle.getUserInfo(okcoinService.userinfo(), TradeCenter.okcoin.name());
        UserInfo huobiUserInfo = apiResultHandle.getUserInfo(huobiService.getAccountInfo(), TradeCenter.huobi.name());
        if (okcoinUserInfo == null) {
            log.error("account_monitor okcoin get user_info error");
            return;
        }
        if (huobiUserInfo == null) {
            log.error("account_monitor huobi get user_info error");
            return;
        }

        BigDecimal okFreeLtc = okcoinUserInfo.getFreeLTC();
        BigDecimal okFreeBtc = okcoinUserInfo.getFreeBTC();

        BigDecimal hbFreeLtc = huobiUserInfo.getFreeLTC();
        BigDecimal hbFreeBtc = huobiUserInfo.getFreeBTC();

        BigDecimal freeCoinOk, freeCoinHb;
        if (AllocationSource.coin == 1) {//btc
            freeCoinOk = okFreeBtc;
            freeCoinHb = hbFreeBtc;
        } else {
            freeCoinOk = okFreeLtc;
            freeCoinHb = hbFreeLtc;
        }

        if (AllocationSource.getReverse()) {//迁移状态中

            BigDecimal tempCoin = (freeCoinHb.add(freeCoinOk)).multiply(stopPercent);

            int hbCompareTo = freeCoinHb.compareTo(tempCoin);
            int okCompareTo = freeCoinOk.compareTo(tempCoin);
            if (hbCompareTo >= 0 && okCompareTo >= 0) {//火币，ok持币都大于总和的40%
                AllocationSource.setReverse(false);
                log.warn("reverse stop , last_reverse_center : " + AllocationSource.getReverseCenter());
            } else if (hbCompareTo >= 0 && okCompareTo == -1) {//火币大于40% ok小于
                if (!AllocationSource.getReverseCenter().equals(TradeCenter.okcoin.name())) {
                    AllocationSource.setReverseCenter(TradeCenter.okcoin.name());
                    log.warn("reverse_center update , now the reverse_center is " + TradeCenter.okcoin.name());
                }
            } else if (hbCompareTo == -1 && okCompareTo >= 0) {//ok大于40% 火币小于
                if (!AllocationSource.getReverseCenter().equals(TradeCenter.huobi.name())) {
                    AllocationSource.setReverseCenter(TradeCenter.huobi.name());
                    log.warn("reverse_center update , now the reverse_center is " + TradeCenter.huobi.name());
                }
            }
        } else {
            BigDecimal tempCoin = (freeCoinHb.add(freeCoinOk)).multiply(startPercent);
            int hbCompareTo = freeCoinHb.compareTo(tempCoin);
            int okCompareTo = freeCoinOk.compareTo(tempCoin);

            if (hbCompareTo == -1) {//火币持币小于总数的10%
                AllocationSource.setReverseCenter(TradeCenter.huobi.name());
                AllocationSource.setReverse(true);
                log.warn("start reverse , the reverse_center is " + TradeCenter.huobi.name());
            } else if (okCompareTo == -1) {//ok持币小于总数10%
                AllocationSource.setReverseCenter(TradeCenter.okcoin.name());
                AllocationSource.setReverse(true);
                log.warn("start reverse , the reverse_center is " + TradeCenter.okcoin.name());
            }
        }
    }

    public static void main(String[] args) {

    }
}
