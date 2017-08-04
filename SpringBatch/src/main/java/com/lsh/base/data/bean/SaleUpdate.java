package com.lsh.base.data.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wuhao on 15/12/30.
 */
public class SaleUpdate implements Serializable {
    private static final long serialVersionUID = 874625371263l;
    private long id;
    private String skuId;
    private int marketId;
    private String ugroup;
    private String sapStatus;
    private int status;
    private int isVaild;
    private String ugroupSys;
    private String shlefNumSys;
    private String shlefNum;
    private long shlefPos;
    private long createdAt;
    private long updatedAt;


    public String getUgroup() {
        return ugroup;
    }

    public void setUgroup(String ugroup) {
        this.ugroup = ugroup;
    }

    public String getSapStatus() {
        return sapStatus;
    }

    public void setSapStatus(String sapStatus) {
        this.sapStatus = sapStatus;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setSupNo(int marketId) {
        this.marketId = marketId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsVaild() {
        return isVaild;
    }

    public void setIsVaild(int isVaild) {
        this.isVaild = isVaild;
    }

    public String getUgroupSys() {
        return ugroupSys;
    }

    public void setUgroupSys(String ugroupSys) {
        this.ugroupSys = ugroupSys;
    }

    public String getShlefNumSys() {
        return shlefNumSys;
    }

    public void setShlefNumSys(String shlefNumSys) {
        this.shlefNumSys = shlefNumSys;
    }

    public String getShlefNum() {
        return shlefNum;
    }

    public void setShlefNum(String shlefNum) {
        this.shlefNum = shlefNum;
    }

    public long getShlefPos() {
        return shlefPos;
    }

    public void setShlefPos(long shlefPos) {
        this.shlefPos = shlefPos;
    }
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public SaleUpdate(){
        this.updatedAt=new Date().getTime()/1000;
        this.createdAt=new Date().getTime()/1000;
        this.isVaild=1;
        this.status=1;
    }

    @Override
    public String toString() {
        return "SaleUpdate{" +
                "id=" + id +
                ", skuId='" + skuId + '\'' +
                ", marketId=" + marketId +
                ", ugroup='" + ugroup + '\'' +
                ", sapStatus='" + sapStatus + '\'' +
                ", status=" + status +
                ", isVaild=" + isVaild +
                ", ugroupSys='" + ugroupSys + '\'' +
                ", shlefNumSys='" + shlefNumSys + '\'' +
                ", shlefNum='" + shlefNum + '\'' +
                ", shlefPos=" + shlefPos +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
