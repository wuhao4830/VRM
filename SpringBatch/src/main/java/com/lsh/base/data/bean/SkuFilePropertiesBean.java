package com.lsh.base.data.bean;

/**
 * Created by wuhao on 15/12/17.
 */
public class SkuFilePropertiesBean {
    private String name;
    private String value;
    private Boolean is_diy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIs_diy() {
        return is_diy;
    }

    public void setIs_diy(Boolean is_diy) {
        this.is_diy = is_diy;
    }

    @Override
    public String toString() {
        return "SkuFilePropertiesBean{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", is_diy=" + is_diy +
                '}';
    }
}
