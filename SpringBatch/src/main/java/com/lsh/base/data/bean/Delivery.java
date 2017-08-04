package com.lsh.base.data.bean;

import java.math.BigDecimal;

/**
 * Created by wuhao on 16/1/11.
 */
public class Delivery {
    private long id;
    private int marketId;
    private String skuId;
    private String kunnr;
    private String werks;
    private String maktx;
    private String mmstaTxt;
    private String zkhspbm;
    private BigDecimal lbkum;
    private String meins;
    private String zdate;
    private String ztime;
    private String zuname;

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getKunnr() {
        return kunnr;
    }

    public void setKunnr(String kunnr) {
        this.kunnr = kunnr;
    }

    public String getWerks() {
        return werks;
    }

    public void setWerks(String werks) {
        this.werks = werks;
    }

    public String getMaktx() {
        return maktx;
    }

    public void setMaktx(String maktx) {
        this.maktx = maktx;
    }

    public String getMmstaTxt() {
        return mmstaTxt;
    }

    public void setMmstaTxt(String mmstaTxt) {
        this.mmstaTxt = mmstaTxt;
    }

    public String getZkhspbm() {
        return zkhspbm;
    }

    public void setZkhspbm(String zkhspbm) {
        this.zkhspbm = zkhspbm;
    }

    public BigDecimal getLbkum() {
        return lbkum;
    }

    public void setLbkum(BigDecimal lbkum) {
        this.lbkum = lbkum;
    }

    public String getMeins() {
        return meins;
    }

    public void setMeins(String meins) {
        this.meins = meins;
    }

    public String getZtime() {
        return ztime;
    }

    public void setZtime(String ztime) {
        this.ztime = ztime;
    }

    public String getZdate() {
        return zdate;
    }

    public void setZdate(String zdate) {
        this.zdate = zdate;
    }

    public String getZuname() {
        return zuname;
    }

    public void setZuname(String zuname) {
        this.zuname = zuname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" +id+
                ", marketId=" + marketId + '\'' +
                ", skuId='" + skuId + '\'' +
                ", kunnr='" + kunnr + '\'' +
                ", verks='" + werks + '\'' +
                ", maktx='" + maktx + '\'' +
                ", mmstaTxt='" + mmstaTxt + '\'' +
                ", zkhspbm='" + zkhspbm + '\'' +
                ", lbkum=" + lbkum +
                ", meins='" + meins + '\'' +
                ", zdate='" + zdate + '\'' +
                ", ztime='" + ztime + '\'' +
                ", zuname='" + zuname + '\'' +
                '}';
    }
}
