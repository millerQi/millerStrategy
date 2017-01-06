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
    private boolean reloadAllocation;
    private boolean synchroAllocation;

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

    public boolean isReloadAllocation() {
        return reloadAllocation;
    }

    public void setReloadAllocation(boolean reloadAllocation) {
        this.reloadAllocation = reloadAllocation;
    }

    public boolean isSynchroAllocation() {
        return synchroAllocation;
    }

    public void setSynchroAllocation(boolean synchroAllocation) {
        this.synchroAllocation = synchroAllocation;
    }
}
