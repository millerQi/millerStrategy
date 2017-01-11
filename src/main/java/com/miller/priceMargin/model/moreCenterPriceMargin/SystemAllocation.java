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
        return "strategyOpen=" + strategyOpen +
                ", reverseMultipleAmount=" + reverseMultipleAmount +
                ", tickAmount=" + tickAmount +
                ", reversePriceMargin=" + reversePriceMargin +
                ", priceMargin=" + priceMargin +
                ", coin=" + coin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemAllocation that = (SystemAllocation) o;

        if (coin != that.coin) return false;
        if (Float.compare(that.reverseMultipleAmount, reverseMultipleAmount) != 0) return false;
        if (strategyOpen != that.strategyOpen) return false;
        if (!priceMargin.equals(that.priceMargin)) return false;
        if (!reversePriceMargin.equals(that.reversePriceMargin)) return false;
        return tickAmount.equals(that.tickAmount);

    }

    @Override
    public int hashCode() {
        int result = coin;
        result = 31 * result + priceMargin.hashCode();
        result = 31 * result + reversePriceMargin.hashCode();
        result = 31 * result + tickAmount.hashCode();
        result = 31 * result + (reverseMultipleAmount != +0.0f ? Float.floatToIntBits(reverseMultipleAmount) : 0);
        result = 31 * result + (strategyOpen ? 1 : 0);
        return result;
    }
}
