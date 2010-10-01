package org.aleksz.ltj;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.rmi.RemoteException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


public class LoggerServiceImplTest {

	private static final String SUMMARY = "some summary";
	private static final String PROJECT = "TST";
	private static final String ISSUE_TYPE = "1";
	private static final String TOKEN = "tokenValue";
	private static final String DUPLICATE_JQL =
		"project = " + PROJECT +
		" AND summary ~ \"\\\"" + SUMMARY + "\\\"\" " +
		" AND status in (Open, \"In Progress\", Reopened)";

	private LoggerService service;
	private Config config;
	private JiraSoapService jiraService;

	@Before
	public void init() {
		config = new Config();
		config.setProject(PROJECT);
		config.setIssueTypeId(ISSUE_TYPE);
		jiraService = EasyMock.createMock(JiraSoapService.class);
		service = new LoggerServiceImpl(config, jiraService);
	}

	@Test
	public void duplicateDoesNotExist() throws org.aleksz.ltj.RemoteException, RemoteException {
		RemoteIssue issue = new RemoteIssue();
		issue.setSummary(SUMMARY);

		expect(jiraService.getIssuesFromJqlSearch(TOKEN, DUPLICATE_JQL, 1))
				.andReturn(new RemoteIssue[] {});
		replay(jiraService);

		assertFalse(service.duplicateExists(issue, TOKEN));
		verify(jiraService);
	}

	@Test
	public void duplicateSummaryExists() throws RemoteException {
		RemoteIssue issue = new RemoteIssue();
		issue.setSummary(SUMMARY);

		expect(jiraService.getIssuesFromJqlSearch(TOKEN, DUPLICATE_JQL, 1))
				.andReturn(new RemoteIssue[] { issue });
		replay(jiraService);

		assertTrue(service.duplicateExists(issue, TOKEN));
		verify(jiraService);
	}

	@Test
	public void createIssueWithoutException() throws RemoteException {
		RemoteIssue result = service.createIssue(createTestLoggingEvent());
		assertEquals("", result.getDescription());
	}

	@Test
	public void createIssue() throws RemoteException {
		Throwable e = new NullPointerException();
		LoggingEvent logEvent = createTestLoggingEvent(e);
		RemoteIssue result = service.createIssue(logEvent);
		assertEquals(config.getProject(), result.getProject());
		assertEquals(config.getIssueTypeId(), result.getType());
		assertEquals(logEvent.getRenderedMessage(), result.getSummary());
		assertEquals(Util.toString(e), result.getDescription());
	}

	private LoggingEvent createTestLoggingEvent(Throwable exception) {
		Logger log = Logger.getLogger(LoggerServiceImplTest.class);
		return new LoggingEvent(null, log, Level.ERROR, "tstmsg", exception);
	}

	private LoggingEvent createTestLoggingEvent() {
		return createTestLoggingEvent(null);
	}
}
