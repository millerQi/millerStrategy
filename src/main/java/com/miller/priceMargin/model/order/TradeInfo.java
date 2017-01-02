package com.miller.priceMargin.model.order;

/**
 * Created by Miller on 2017/1/1.
 */
public class TradeInfo {
    public TradeInfo(){}
    public TradeInfo(long orderId, String result) {
        this.orderId = orderId;
        this.result = result;
    }

    private long orderId;
    private String result;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "result='" + result + '\'' +
                ", orderId=" + orderId +
                '}';
    }
}
