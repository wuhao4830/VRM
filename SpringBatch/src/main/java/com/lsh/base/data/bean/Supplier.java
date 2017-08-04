package com.lsh.base.data.bean;

/**
 * Created by wuhao on 15/12/30.
 */
public class Supplier {
    private long id;
    private int marketId;
    private String name;
    private String supNo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupNo() {
        return supNo;
    }

    public void setSupNo(String supNo) {
        this.supNo = supNo;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", marketId=" + marketId +
                ", name='" + name + '\'' +
                ", supNo='" + supNo + '\'' +
                '}';
    }
}

