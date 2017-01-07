package com.miller.priceMargin.model.order;

import java.math.BigDecimal;

/**
 * Created by tonyqi on 17-1-5.
 */
public class UserInfo {
    private BigDecimal freeBTC;
    private BigDecimal freeLTC;
    private BigDecimal freeCny;
    private BigDecimal borrowBTC;
    private BigDecimal borrowLTC;
    private BigDecimal borrowPrice;
    private BigDecimal netAsset;


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

    public BigDecimal getBorrowBTC() {
        return borrowBTC;
    }

    public void setBorrowBTC(BigDecimal borrowBTC) {
        this.borrowBTC = borrowBTC;
    }

    public BigDecimal getBorrowLTC() {
        return borrowLTC;
    }

    public void setBorrowLTC(BigDecimal borrowLTC) {
        this.borrowLTC = borrowLTC;
    }

    public BigDecimal getBorrowPrice() {
        return borrowPrice;
    }

    public void setBorrowPrice(BigDecimal borrowPrice) {
        this.borrowPrice = borrowPrice;
    }

    public BigDecimal getNetAsset() {
        return netAsset;
    }

    public void setNetAsset(BigDecimal netAsset) {
        this.netAsset = netAsset;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "freeBTC=" + freeBTC +
                ", freeLTC=" + freeLTC +
                ", freeCny=" + freeCny +
                ", borrowBTC=" + borrowBTC +
                ", borrowLTC=" + borrowLTC +
                ", borrowPrice=" + borrowPrice +
                ", netAsset=" + netAsset +
                '}';
    }
}
