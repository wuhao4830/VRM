package com.lsh.base.data.bean;

/**
 * Created by wuhao on 16/3/30.
 */
public class OldSkukey {
    public String skuId;
    public int marketId;
    public long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OldSkukey{" +
                "skuId='" + skuId + '\'' +
                ", marketId=" + marketId +
                ", id=" + id +
                '}';
    }
}
