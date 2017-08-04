package com.jira.entity;

import java.io.Serializable;

/**
 *
 * <p>
 *用户实体对象定义
 * </p>
 *
 * @author 吴昊
 *
 */
public class IssueInfo implements Serializable {

	private static final long serialVersionUID = 7289036533757178921L;
	private String username;
	private String password;
	private IssueBean issue;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public IssueBean getIssue() {
		return issue;
	}

	public void setIssue(IssueBean issue) {
		this.issue = issue;
	}

	@Override
	public String toString() {
		return "IssueInfo{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", issue=" + issue +
				'}';
	}
}
