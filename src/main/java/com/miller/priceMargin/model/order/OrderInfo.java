package com.miller.priceMargin.model.order;

import java.math.BigDecimal;

/**
 * Created by Miller on 2017/1/1.
 */
public class OrderInfo {
    public OrderInfo() {
    }

    public OrderInfo(BigDecimal amount, BigDecimal avgPrice, BigDecimal dealAmount, BigDecimal price, String result, String tradeDirection) {
        this.amount = amount;
        this.avgPrice = avgPrice;
        this.dealAmount = dealAmount;
        this.result = result;
        this.price = price;
        this.tradeDirection = tradeDirection;
    }

    private BigDecimal amount;
    private BigDecimal avgPrice;
    private BigDecimal dealAmount;
    private String result;
    private BigDecimal price;
    private String tradeDirection;

    public String getTradeDirection() {
        return tradeDirection;
    }

    public void setTradeDirection(String tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "amount=" + amount +
                ", avgPrice=" + avgPrice +
                ", dealAmount=" + dealAmount +
                ", result='" + result + '\'' +
                ", price=" + price +
                '}';
    }
}
