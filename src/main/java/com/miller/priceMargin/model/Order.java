package com.miller.priceMargin.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Miller on 2016/12/24.
 */
public class Order {
    private Long id;
    private String tradeCenter;//交易中心
    private String tradeDirection;//交易方向
    private BigDecimal tickerPrice;//准备开仓价格
    private BigDecimal amount;//挂单数量
    private BigDecimal dealAmount;//实际成交量
    private BigDecimal dealPrice;//实际成交平均成交价
    private Date createTime;//订单创建时间


    public String getTradeCenter() {
        return tradeCenter;
    }

    public void setTradeCenter(String tradeCenter) {
        this.tradeCenter = tradeCenter;
    }

    public String getTradeDirection() {
        return tradeDirection;
    }

    public void setTradeDirection(String tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public BigDecimal getTickerPrice() {
        return tickerPrice;
    }

    public void setTickerPrice(BigDecimal tickerPrice) {
        this.tickerPrice = tickerPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
