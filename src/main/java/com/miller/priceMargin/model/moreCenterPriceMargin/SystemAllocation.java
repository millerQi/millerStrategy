package com.miller.priceMargin.model.moreCenterPriceMargin;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-6.
 */
public class SystemAllocation {
    private int coin;
    private BigDecimal priceMargin;
    private BigDecimal reversePriceMargin;
    private BigDecimal tickAmount;
    private float reverseMultipleAmount;
    private boolean strategyOpen;

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public BigDecimal getPriceMargin() {
        return priceMargin;
    }

    public void setPriceMargin(BigDecimal priceMargin) {
        this.priceMargin = priceMargin;
    }

    public BigDecimal getReversePriceMargin() {
        return reversePriceMargin;
    }

    public void setReversePriceMargin(BigDecimal reversePriceMargin) {
        this.reversePriceMargin = reversePriceMargin;
    }

    public BigDecimal getTickAmount() {
        return tickAmount;
    }

    public void setTickAmount(BigDecimal tickAmount) {
        this.tickAmount = tickAmount;
    }

    public float getReverseMultipleAmount() {
        return reverseMultipleAmount;
    }

    public void setReverseMultipleAmount(float reverseMultipleAmount) {
        this.reverseMultipleAmount = reverseMultipleAmount;
    }

    public boolean isStrategyOpen() {
        return strategyOpen;
    }

    public void setStrategyOpen(boolean strategyOpen) {
        this.strategyOpen = strategyOpen;
    }

    @Override
    public String toString() {
        return "SystemAllocation{" +
                "strategyOpen=" + strategyOpen +
                ", reverseMultipleAmount=" + reverseMultipleAmount +
                ", tickAmount=" + tickAmount +
                ", reversePriceMargin=" + reversePriceMargin +
                ", priceMargin=" + priceMargin +
                ", coin=" + coin +
                '}';
    }
}
