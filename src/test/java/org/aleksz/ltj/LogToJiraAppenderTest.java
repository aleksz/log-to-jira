package org.aleksz.ltj;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.rmi.RemoteException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;


public class LogToJiraAppenderTest {

	private static final String TOKEN = "sdfagsdf";
	private static final String USERNAME = "theUser";
	private static final String PASS = "thePass";

	private LogToJiraAppender appender;
	private AppenderService appenderService;
	private JiraSoapService jiraService;

	@Before
	public void init() {

		appenderService = createMock(AppenderService.class);
		jiraService = createMock(JiraSoapService.class);

		appender = new LogToJiraAppender() {

			@Override
			protected AppenderService getService() {
				return appenderService;
			}

			@Override
			protected JiraSoapService getJiraService() {
				return jiraService;
			}

		};

		appender.setUsername(USERNAME);
		appender.setPassword(PASS);
	}

	@Test
	public void append() throws RemoteAuthenticationException, RemoteException, RemoteException {
		LoggingEvent logEvent = createTestLoggingEvent();
		RemoteIssue issue = new RemoteIssue();

		expect(jiraService.login(USERNAME, PASS)).andReturn(TOKEN);
		expect(appenderService.createIssue(logEvent)).andReturn(issue);
		expect(appenderService.duplicateExists(issue, TOKEN)).andReturn(false);
		expect(jiraService.createIssue(TOKEN, issue)).andReturn(issue);
		expect(jiraService.logout(TOKEN)).andReturn(true);
		replay(jiraService);
		replay(appenderService);

		appender.append(logEvent);

		verify(jiraService);
		verify(appenderService);
	}

	@Test
	public void appendDuplicate() throws RemoteAuthenticationException, org.aleksz.ltj.RemoteException, RemoteException {
		LoggingEvent logEvent = createTestLoggingEvent();
		RemoteIssue issue = new RemoteIssue();

		expect(jiraService.login(USERNAME, PASS)).andReturn(TOKEN);
		expect(appenderService.createIssue(logEvent)).andReturn(issue);
		expect(appenderService.duplicateExists(issue, TOKEN)).andReturn(true);
		expect(jiraService.logout(TOKEN)).andReturn(true);
		replay(jiraService);
		replay(appenderService);

		appender.append(logEvent);

		verify(jiraService);
		verify(appenderService);
	}

	private LoggingEvent createTestLoggingEvent() {
		Logger log = Logger.getLogger(LogToJiraAppenderTest.class);
		return new LoggingEvent(null, log, Level.ERROR, "tstmsg", null);
	}
}
