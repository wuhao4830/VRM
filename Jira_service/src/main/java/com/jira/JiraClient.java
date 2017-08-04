package com.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.jira.entity.ErrorMsg;
import com.jira.entity.IssueBean;
import com.jira.entity.IssueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * jira java工具类
 * jira-rest-java-client-2.0.0-m2.jar
 * @author wuhao
 *
 */
public class JiraClient {
    private static String uri = "http://jira.lsh123.com";
    private static String user = "wuhao4830";
    private static String pwd = "4830967";
    private  JiraRestClient restClient;
    private static final Logger logger = LoggerFactory.getLogger(JiraClient.class);
    private static final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

    public JiraClient(String username,String password)throws URISyntaxException{
        URI jiraServerUri = new URI(uri);
        if(username!=null&&password!=null){
            restClient = factory.createWithBasicHttpAuthentication(
                    jiraServerUri, username, password);
        }else {
            restClient = factory.createWithBasicHttpAuthentication(
                    jiraServerUri, user, pwd);
        }
    }
    public JiraClient()throws URISyntaxException{
        URI jiraServerUri = new URI(uri);
        restClient = factory.createWithBasicHttpAuthentication(
                jiraServerUri, user, pwd);

    }

    public JiraRestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(JiraRestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * 获得工单信息
     *
     * @param issueKey
     *            工单key，比如：NQCP-5
     * @throws URISyntaxException
     */
    public Issue getIssue(String issueKey) throws URISyntaxException {
        Issue issue = null;
        // get issue through issueKey
        try {
            issue = restClient.getIssueClient().getIssue(issueKey).claim();
            // 打印工单后续的工作流
            Iterable<Transition> iter = getTransitionByIssue(issue);
            logger.info("transition:",iter);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        // 打印工单明细
        logger.info("issue info:",issue);
        return issue;

    }
    public Iterable<Transition> getTransitionByIssue(Issue issue) throws URISyntaxException{
        Iterable<Transition> iter = restClient.getIssueClient()
                .getTransitions(issue).claim();
        return iter;
    }

    /**
     * 检索工单
     * @param jql
     * @return
     * @throws URISyntaxException
     */
    public Iterable<BasicIssue> searchIssues(String jql) throws URISyntaxException{
        SearchResult searchResutl = restClient.getSearchClient().searchJql(jql).claim();
        Iterable<BasicIssue> iter = searchResutl.getIssues();
        return iter;
    }

    /**
     * 检索工单
     * @param jql
     * @param startIndex
     * @param maxResults
     * @return
     * @throws URISyntaxException
     */
    public Iterable<BasicIssue> searchIssues(String jql,int startIndex, int maxResults) throws URISyntaxException{
        SearchResult searchResutl = restClient.getSearchClient().searchJql(jql,maxResults,startIndex).claim();
        Iterable<BasicIssue> iter = searchResutl.getIssues();
        return iter;
    }


    /**
     * 打印jira系统中已经创建的全部issueType
     * issuetype/
     *
     * @throws URISyntaxException
     */
    public Iterable<IssueType> printAllIssueType() throws URISyntaxException {
        Iterable<IssueType> iter = restClient.getMetadataClient()
                .getIssueTypes().claim();
        return iter;

    }
    /**
     * 创建一个新工单
     *
     * @param projectKey
     *            项目key，比如：NQCP
     * @param issueType
     *            工单类型，来源于printAllIssueType()的id
     * @param description
     *            工单描述
     * @param summary
     *            工单主题
     * @param assignee
     *            工单负责人
     * @throws URISyntaxException
     */
    public  BasicIssue createIssue(String projectKey, Long issueType, String description,String summary,String assignee)
            throws Exception {

        IssueInputBuilder issueBuilder = new IssueInputBuilder(projectKey,
                issueType);

        issueBuilder.setDescription(description);
        issueBuilder.setSummary(summary);
        if (getUser(assignee) != null) {

            issueBuilder.setAssigneeName(assignee);
        }
        IssueInput issueInput = issueBuilder.build();

        BasicIssue  bIssue = restClient.getIssueClient().createIssue(issueInput)
                    .claim();
        return bIssue;
    }
    /**
     * 创建一个错误工单
     * @param msg
     */
    public  BasicIssue createIssue(ErrorMsg msg) throws URISyntaxException {
        logger.info("create errorIssue:",msg);

        IssueInputBuilder issueBuilder = new IssueInputBuilder("ERROR",
                1l);

        issueBuilder.setDescription(msg.toString());
        issueBuilder.setSummary("创建异常工单失败");
        IssueInput issueInput = issueBuilder.build();

        BasicIssue bIssue = null;
        try {
            bIssue = restClient.getIssueClient().createIssue(issueInput)
                    .claim();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

        return bIssue;
    }
    public  BasicIssue createIssue(IssueInfo info) throws Exception {
        logger.info(String.format("create Issue:%s",info));
        IssueBean bean=info.getIssue();
        long type=1l;
        if(bean.getIssueType()!=null){
            type=bean.getIssueType();
        }
        IssueInputBuilder issueBuilder = new IssueInputBuilder(bean.getProjectKey(),
                type);
        issueBuilder.setDescription(bean.getDescription());
        issueBuilder.setSummary(bean.getSummary());
        if(bean.getAssignee()!=null){
            if (getUser(bean.getAssignee()) != null) {
                issueBuilder.setAssigneeName(bean.getAssignee());
            }
        }
        if(bean.getReporter()!=null) {
            if (getUser(bean.getReporter()) != null) {
                issueBuilder.setReporterName(bean.getReporter());
            }
        }
        if(bean.getDueDate()!=null){
            issueBuilder.setDueDate(bean.getDueDate());
        }
        IssueInput issueInput = issueBuilder.build();

        BasicIssue bIssue = restClient.getIssueClient().createIssue(issueInput).claim();

        return bIssue;
    }
    /**
     * 创建一个工单，
     * @param bean 工单实体
     */
    public  BasicIssue createIssue(IssueBean bean) throws URISyntaxException {
        logger.info("create Issue:",bean);

        IssueInputBuilder issueBuilder = new IssueInputBuilder(bean.getProjectKey(),
                bean.getIssueType());

        issueBuilder.setDescription(bean.getDescription());
        issueBuilder.setSummary(bean.getSummary());
        if (getUser(bean.getAssignee()) != null) {

            issueBuilder.setAssigneeName(bean.getAssignee());
        }
        if (getUser(bean.getReporter())!=null) {
            issueBuilder.setReporterName(bean.getReporter());
        }
        if(bean.getDueDate()!=null){
            issueBuilder.setDueDate(bean.getDueDate());
        }
        IssueInput issueInput = issueBuilder.build();

        BasicIssue bIssue = null;

        try {
            bIssue = restClient.getIssueClient().createIssue(issueInput)
                    .claim();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

        return bIssue;
    }

    /**
     * 获取用户信息
     *
     * @param username
     * @return
     * @throws URISyntaxException
     */
    public User getUser(String username) throws URISyntaxException {
        User user = null;
        try {
            user = restClient.getUserClient().getUser(username).claim();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        logger.info("get user:",user);
        return user;
    }

    /**
     * 改变工单workflow状态 issue的workflow是不可以随便改变的，必须按照流程图的顺序进行改变，具体如下：
     *
     * 当前状态 ：说明                      变更流程id:说明 >> 变更后状态
     1:open，开放                          1)4:start progress >> in progerss 2)5:resolve issue >> resolved 3)2:close issue >> closed
     3:in progerss 正在处理                1)301:stop progress >> open 2)5:resolve issue >> resolved 3)2:close issue >> closed
     4:resolved 已解决                     1)701:close issue >> closed 2)3:reopen issue >> reopened
     5:reopened 重新打开                   1)4:start progress >> in progerss 2)5:resolve issue >> resolved 3)2:close issue >> closed
     6:closed 已关闭                       1)3:reopen issue >> reopened
     *
     *
     * 可通过如下方式查看当前工单的后续工作流程： Iterable<Transition> iter =
     * restClient.getIssueClient().getTransitions(issue).claim();
     *
     * for (Transition transition : iter) { System.out.println(transition); }
     *
     * 输出结果：当前工单状态是 5:reopened 重新打开 Transition{id=4, name=Start Progress,
     * fields=[]} Transition{id=5, name=Resolve Issue,
     * fields=[Field{id=fixVersions, isRequired=false, type=array},
     * Field{id=resolution, isRequired=true, type=resolution}]} Transition{id=2,
     * name=Close Issue, fields=[Field{id=fixVersions, isRequired=false,
     * type=array}, Field{id=resolution, isRequired=true, type=resolution}]}
     *
     *
     * @param issuekey
     *            工单key
     * @param statusId
     *            变更流程id
     * @throws URISyntaxException
     */
    public void changeIssueStatus(String issuekey, int statusId)
            throws URISyntaxException {

        Issue issue = getIssue(issuekey);
        if (issue != null) {
            TransitionInput tinput = new TransitionInput(statusId);
            restClient.getIssueClient().transition(issue, tinput);
        }

    }

}