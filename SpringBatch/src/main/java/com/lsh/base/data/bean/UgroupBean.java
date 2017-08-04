package com.lsh.base.data.bean;

/**
 * Created by wuhao on 16/1/13.
 */
public class UgroupBean {
    private String skuId;
    private String shelfNum;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getShelfNum() {
        return shelfNum;
    }

    public void setShelfNum(String shelfNum) {
        this.shelfNum = shelfNum;
    }

    @Override
    public String toString() {
        return "UgroupBean{" +
                "skuId='" + skuId + '\'' +
                ", shelfNum='" + shelfNum + '\'' +
                '}';
    }
}
