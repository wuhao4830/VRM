package com.lsh.base.data.bean;

/**
 * Created by wuhao on 16/3/29.
 */
public class Status {
    String skuId;
    int marketId;


    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    @Override
    public String toString() {
        return "Status{" +
                ", skuId='" + skuId + '\'' +
                ", marketId=" + marketId +
                '}';
    }
}
