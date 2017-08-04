package com.lsh.base.data.bean;

import java.util.Map;

/**
 * Created by wuhao on 16/1/13.
 */
public class PackBean {
    private String skuId;
    private Map<String ,String> packSizeMap;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Map<String, String> getPackSizeMap() {
        return packSizeMap;
    }

    public void setPackSizeMap(Map<String, String> packSizeMap) {
        this.packSizeMap = packSizeMap;
    }

    @Override
    public String toString() {
        return "PackBean{" +
                "skuId='" + skuId + '\'' +
                ", packSizeMap=" + packSizeMap +
                '}';
    }
}
