package org.aleksz.ltj;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LogToJiraAppender extends AppenderSkeleton {

	private String username;
	private String password;
	private String project;
	private JiraSoapServiceServiceLocator jiraSoapServiceServiceLocator;
	private JiraSoapService jiraSoapService;

	public LogToJiraAppender() {
		super();
		jiraSoapServiceServiceLocator = new JiraSoapServiceServiceLocator();
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent loggingevent) {
		try {
			String token = jiraSoapService.login(username, password);
			RemoteIssueType[] issueTypes = jiraSoapService.getIssueTypes(token);
			RemoteIssue issue = new RemoteIssue();
			issue.setProject(project);
			issue.setType(issueTypes[0].getId());
			issue.setSummary(loggingevent.getRenderedMessage());
			jiraSoapService.createIssue(token, issue);
			jiraSoapService.logout(token);
		} catch (RemoteAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUrl(String url) {
		try {
			this.jiraSoapService = jiraSoapServiceServiceLocator.getJirasoapserviceV2(new URL(url));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProject(String project) {
		this.project = project;
	}
}
