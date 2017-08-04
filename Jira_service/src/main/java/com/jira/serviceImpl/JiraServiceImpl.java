package com.jira.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.jira.JiraClient;
import com.jira.entity.ErrorMsg;
import com.jira.entity.IssueInfo;
import com.jira.service.JiraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuhao on 15/12/10.
 */
public class JiraServiceImpl implements JiraService {
    private static final Logger logger = LoggerFactory.getLogger(JiraServiceImpl.class);
    private JiraClient jiraClient;

    public JiraClient getJiraClient() {
        return jiraClient;
    }

    public void setJiraClient(JiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    public JiraServiceImpl() {

    }
    public boolean newJiraIssue(String message) throws Exception {

        IssueInfo info = formatMsg(message);
        if (info != null) {
            if(info.getPassword()!=null&&info.getUsername()!=null){
                jiraClient=new JiraClient(info.getUsername(),info.getPassword());
            }else {
                jiraClient=new JiraClient();
            }
            jiraClient.createIssue(info);
            logger.info(String.format("create Issue:%s", info.toString()));
            return true;
        }
        return false;
    }
    public void saveError(ErrorMsg msg){
        try {
            jiraClient = new JiraClient();
            jiraClient.createIssue(msg);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public IssueInfo formatMsg(String message) throws Exception{
        logger.info(String.format("get message:%s", message));
            IssueInfo info= JSON.parseObject(message,IssueInfo.class);
            return info;

    }
}
