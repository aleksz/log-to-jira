package org.aleksz.ltj;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

public class LogToJiraAppender extends AppenderSkeleton {

	private Config config = new Config();
	private BugReport bugReport = new BugReport(config);
	private JiraSoapServiceServiceLocator jiraSoapServiceServiceLocator;
	private JiraSoapService jiraSoapService;

	public LogToJiraAppender() {
		jiraSoapServiceServiceLocator = new JiraSoapServiceServiceLocator();
	}

	@Override
	protected void append(LoggingEvent loggingEvent) {
		try {

			String token = jiraSoapService.login(config.getUsername(), config.getPassword());
			RemoteIssue issue = bugReport.getIssue(loggingEvent);
			if (!isDuplicate(issue, token)) {
				jiraSoapService.createIssue(token, issue);
			}
			jiraSoapService.logout(token);

		} catch (RemoteAuthenticationException e) {
			errorHandler.error("JIRA auth failed", e, ErrorCode.GENERIC_FAILURE, loggingEvent);
		} catch (RemoteException e) {
			errorHandler.error("JIRA problem", e, ErrorCode.GENERIC_FAILURE, loggingEvent);
		}
	}

	private boolean isDuplicate(RemoteIssue issue, String token) throws org.aleksz.ltj.RemoteException, RemoteException {
		String JQL =
			"project = " + config.getProject() +
			" AND summary ~ \"\\\"" + issue.getSummary() + "\\\"\" " +
			" AND status in (Open, \"In Progress\", Reopened)";
		return jiraSoapService.getIssuesFromJqlSearch(token, JQL, 1).length > 0;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	public void setUrl(String url) {
		try {
			this.jiraSoapService = jiraSoapServiceServiceLocator.getJirasoapserviceV2(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		} catch (ServiceException e) {
			errorHandler.error("JIRA connection problem", e, ErrorCode.GENERIC_FAILURE);
		}
	}

	public void setUsername(String username) {
		config.setUsername(username);
	}

	public void setPassword(String password) {
		config.setPassword(password);
	}

	public void setProject(String project) {
		config.setProject(project);
	}

	public void setIssueTypeId(String issueTypeId) {
		config.setIssueTypeId(issueTypeId);
	}
}
