package com.lsh.base.data.bean;

/**
 * Created by wuhao on 15/12/17.
 */
public class PropertiesBean {
    private String name;
    private String value;

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

    public PropertiesBean(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
