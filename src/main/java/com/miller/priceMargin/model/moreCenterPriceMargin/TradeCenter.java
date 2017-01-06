package com.miller.priceMargin.model.moreCenterPriceMargin;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-6.
 */
public class TradeCenter {
    private String centerName;
    private BigDecimal netAsset;
    private BigDecimal freeAsset;
    private BigDecimal freeAmount;
    private BigDecimal borrowAmount;
    private BigDecimal borrowPrice;

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public BigDecimal getNetAsset() {
        return netAsset;
    }

    public void setNetAsset(BigDecimal netAsset) {
        this.netAsset = netAsset;
    }

    public BigDecimal getFreeAsset() {
        return freeAsset;
    }

    public void setFreeAsset(BigDecimal freeAsset) {
        this.freeAsset = freeAsset;
    }

    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    public BigDecimal getBorrowAmount() {
        return borrowAmount;
    }

    public void setBorrowAmount(BigDecimal borrowAmount) {
        this.borrowAmount = borrowAmount;
    }

    public BigDecimal getBorrowPrice() {
        return borrowPrice;
    }

    public void setBorrowPrice(BigDecimal borrowPrice) {
        this.borrowPrice = borrowPrice;
    }
}
