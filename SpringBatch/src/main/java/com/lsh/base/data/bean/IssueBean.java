package com.lsh.base.data.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wuhao on 15/12/9.
 */
public class IssueBean implements Serializable {
    private static final long serialVersionUID = 1234203487L;
    private Long issueType;
    private String projectKey;
    private String summary;
    private String description;
    private String reporter;
    private String assignee;
    private Date dueDate;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Long getIssueType() {
        return issueType;
    }

    public void setIssueType(Long issueType) {
        this.issueType = issueType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "IssueBean{" +
                "issueType=" + issueType +
                ", projectKey='" + projectKey + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", reporter='" + reporter + '\'' +
                ", assignee='" + assignee + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
    public IssueBean(){
        issueType=1l;
        summary="数据导入异常";
    }
}
