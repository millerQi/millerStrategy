package com.miller.priceMargin.model.order;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-5.
 */
public class UserInfo {
    private BigDecimal freeBTC;
    private BigDecimal freeLTC;
    private BigDecimal freeCny;


    public BigDecimal getFreeBTC() {
        return freeBTC;
    }

    public void setFreeBTC(BigDecimal freeBTC) {
        this.freeBTC = freeBTC;
    }

    public BigDecimal getFreeLTC() {
        return freeLTC;
    }

    public void setFreeLTC(BigDecimal freeLTC) {
        this.freeLTC = freeLTC;
    }

    public BigDecimal getFreeCny() {
        return freeCny;
    }

    public void setFreeCny(BigDecimal freeCny) {
        this.freeCny = freeCny;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "freeBTC=" + freeBTC +
                ", freeLTC=" + freeLTC +
                ", freeCny=" + freeCny +
                '}';
    }
}
