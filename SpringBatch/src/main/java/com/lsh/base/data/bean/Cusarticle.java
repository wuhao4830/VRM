package com.lsh.base.data.bean;

/**
 * Created by wuhao on 15/12/30.
 */
public class Cusarticle {
    private long id;
    private String mandt;
    private String vkorg;
    private String vtweg;
    private String kunnr;
    private String kdmat;
    private String skuId;
    private String kdptx;
    private float kbetr;
    private String mmsta;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMandt() {
        return mandt;
    }

    public void setMandt(String mandt) {
        this.mandt = mandt;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

    public String getVtweg() {
        return vtweg;
    }

    public void setVtweg(String vtweg) {
        this.vtweg = vtweg;
    }

    public String getKunnr() {
        return kunnr;
    }

    public void setKunnr(String kunnr) {
        this.kunnr = kunnr;
    }

    public String getKdmat() {
        return kdmat;
    }

    public void setKdmat(String kdmat) {
        this.kdmat = kdmat;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public float getKbetr() {
        return kbetr;
    }

    public void setKbetr(float kbetr) {
        this.kbetr = kbetr;
    }

    public String getKdptx() {
        return kdptx;
    }

    public void setKdptx(String kdptx) {
        this.kdptx = kdptx;
    }

    public String getMmsta() {
        return mmsta;
    }

    public void setMmsta(String mmsta) {
        this.mmsta = mmsta;
    }

    @Override
    public String toString() {
        return "Cusarticle{" +
                "id=" + id +
                ", mandt='" + mandt + '\'' +
                ", vkorg='" + vkorg + '\'' +
                ", vtweg=" + vtweg +
                ", kunnr='" + kunnr + '\'' +
                ", kdmat=" + kdmat +
                ", skuId='" + skuId + '\'' +
                ", kdptx='" + kdptx + '\'' +
                ", kbetr=" + kbetr +
                ", mmsta=" + mmsta +
                '}';
    }
}
