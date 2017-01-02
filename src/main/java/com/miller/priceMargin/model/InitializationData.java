package com.miller.priceMargin.model;

import java.math.BigDecimal;

/**
 *
 * Created by Miller on 2016/12/24.
 */
public class InitializationData {//初始化数据相关
    private BigDecimal gains;//总盈利
    private int hasCoin;//持币方 -1 为huobi持币，1为okcoin持币 不用boolean考虑到以后可能多家交易所套利
    private BigDecimal okFreePrice;//程序初始时Ok可用金额
    private BigDecimal okLastPrice;
    private BigDecimal hbFreePrice;//程序初始时hb可用金额
    private BigDecimal hbLastPrice;

    public BigDecimal getGains() {
        return gains;
    }

    public void setGains(BigDecimal gains) {
        this.gains = gains;
    }

    public int getHasCoin() {
        return hasCoin;
    }

    public void setHasCoin(int hasCoin) {
        this.hasCoin = hasCoin;
    }

    public BigDecimal getOkFreePrice() {
        return okFreePrice;
    }

    public void setOkFreePrice(BigDecimal okFreePrice) {
        this.okFreePrice = okFreePrice;
    }

    public BigDecimal getHbFreePrice() {
        return hbFreePrice;
    }

    public void setHbFreePrice(BigDecimal hbFreePrice) {
        this.hbFreePrice = hbFreePrice;
    }

    public BigDecimal getOkLastPrice() {
        return okLastPrice;
    }

    public void setOkLastPrice(BigDecimal okLastPrice) {
        this.okLastPrice = okLastPrice;
    }

    public BigDecimal getHbLastPrice() {
        return hbLastPrice;
    }

    public void setHbLastPrice(BigDecimal hbLastPrice) {
        this.hbLastPrice = hbLastPrice;
    }
}
