package com.lsh.base.data.bean;

/**
 * Created by wuhao on 16/3/30.
 */
public class OldSalekey {
    public String skuId;
    public int marketId;
    public String supNo;

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

    public String getSupNo() {
        return supNo;
    }

    public void setSupNo(String supNo) {
        this.supNo = supNo;
    }

    @Override
    public String toString() {
        return "OldSalekey{" +
                "skuId='" + skuId + '\'' +
                ", marketId=" + marketId +
                ", supNo='" + supNo + '\'' +
                '}';
    }
}
