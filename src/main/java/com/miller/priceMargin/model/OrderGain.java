package com.miller.priceMargin.model;

import java.math.BigDecimal;

/**
 * Created by Miller on 2016/12/24.
 */
public class OrderGain {//套利订单关联表-相当于日志表
    private Long id;
    private Long sellOrderId;//卖方订单ID
    private BigDecimal gains;//单笔套利盈利
    private Long buyOrderId;//买方订单ID
    private BigDecimal okFreePrice;//交易后ok可用资金
    private BigDecimal hbFreePrice;//交易后hb可用资金

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(Long sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public BigDecimal getGains() {
        return gains;
    }

    public void setGains(BigDecimal gains) {
        this.gains = gains;
    }

    public Long getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(Long buyOrderId) {
        this.buyOrderId = buyOrderId;
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
}
