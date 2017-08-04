package com.jira.service;

import com.jira.entity.ErrorMsg;

/**
 * Created by wuhao on 15/12/9.
 */
public interface JiraService {
    public boolean newJiraIssue(String message)throws Exception;
    public void saveError(ErrorMsg mSg);

}
