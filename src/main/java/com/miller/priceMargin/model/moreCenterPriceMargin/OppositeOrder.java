package com.miller.priceMargin.model.moreCenterPriceMargin;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by tonyqi on 17-1-6.
 */
public class OppositeOrder {
    private String sellCenter;
    private BigDecimal sellAvgPrice;
    private BigDecimal sellAmount;
    private String buyCenter;
    private BigDecimal buyAvgPrice;
    private BigDecimal buyAmount;
    private BigDecimal gains;
    private Timestamp createTime;

    public String getSellCenter() {
        return sellCenter;
    }

    public void setSellCenter(String sellCenter) {
        this.sellCenter = sellCenter;
    }

    public BigDecimal getSellAvgPrice() {
        return sellAvgPrice;
    }

    public void setSellAvgPrice(BigDecimal sellAvgPrice) {
        this.sellAvgPrice = sellAvgPrice;
    }

    public BigDecimal getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(BigDecimal sellAmount) {
        this.sellAmount = sellAmount;
    }

    public String getBuyCenter() {
        return buyCenter;
    }

    public void setBuyCenter(String buyCenter) {
        this.buyCenter = buyCenter;
    }

    public BigDecimal getBuyAvgPrice() {
        return buyAvgPrice;
    }

    public void setBuyAvgPrice(BigDecimal buyAvgPrice) {
        this.buyAvgPrice = buyAvgPrice;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigDecimal getGains() {
        return gains;
    }

    public void setGains(BigDecimal gains) {
        this.gains = gains;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
