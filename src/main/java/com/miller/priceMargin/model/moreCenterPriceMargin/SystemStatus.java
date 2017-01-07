package com.miller.priceMargin.model.moreCenterPriceMargin;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-6.
 */
public class SystemStatus {
    private BigDecimal allGains;
    private BigDecimal coinSellCount;
    private int gainsOrderCount;
    private int lossOrderCount;

    public BigDecimal getAllGains() {
        return allGains;
    }

    public void setAllGains(BigDecimal allGains) {
        this.allGains = allGains;
    }

    public BigDecimal getCoinSellCount() {
        return coinSellCount;
    }

    public void setCoinSellCount(BigDecimal coinSellCount) {
        this.coinSellCount = coinSellCount;
    }

    public int getGainsOrderCount() {
        return gainsOrderCount;
    }

    public void setGainsOrderCount(int gainsOrderCount) {
        this.gainsOrderCount = gainsOrderCount;
    }

    public int getLossOrderCount() {
        return lossOrderCount;
    }

    public void setLossOrderCount(int lossOrderCount) {
        this.lossOrderCount = lossOrderCount;
    }

}
