package com.lsh.base.data.bean;

/**
 * Created by wuhao on 16/1/8.
 */
public class SkuFileBean {
    private String barCode;
    private String properties;
    private String imgs;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    @Override
    public String toString() {
        return "SkuFileBean{" +
                "barCode='" + barCode + '\'' +
                ", properties='" + properties + '\'' +
                ", imgs='" + imgs + '\'' +
                '}';
    }
}
