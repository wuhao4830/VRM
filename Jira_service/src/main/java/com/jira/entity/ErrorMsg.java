package com.jira.entity;

import java.io.Serializable;

/**
 * Created by wuhao on 15/12/18.
 */
public class ErrorMsg implements Serializable{
    private static final long serialVersionUID = 42567785211L;
    private String errorinfo;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getErrorinfo() {
        return errorinfo;
    }

    public void setErrorinfo(String errorinfo) {
        this.errorinfo = errorinfo;
    }

    @Override
    public String toString() {
        return "ErrorMsg{" +
                "errorinfo='" + errorinfo + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
