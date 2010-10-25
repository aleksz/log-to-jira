package org.aleksz.ltj;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.aleksz.ltj.plugin.MDCPlugin;
import org.aleksz.ltj.plugin.TimestampPlugin;
import org.aleksz.ltj.soap.JiraSoapService;
import org.aleksz.ltj.soap.JiraSoapServiceServiceLocator;
import org.aleksz.ltj.soap.RemoteAuthenticationException;
import org.aleksz.ltj.soap.RemoteComment;
import org.aleksz.ltj.soap.RemoteException;
import org.aleksz.ltj.soap.RemoteIssue;
import org.aleksz.ltj.soap.RemotePermissionException;
import org.aleksz.ltj.soap.RemoteValidationException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IntegrationTest {

	private static final Logger LOG = Logger.getLogger(IntegrationTest.class);

	private JiraSoapService jiraSoapService;
	private String token;

	@Before
	public void init() throws MalformedURLException, ServiceException,
			RemoteAuthenticationException, RemoteException, java.rmi.RemoteException {

		JiraSoapServiceServiceLocator locator = new JiraSoapServiceServiceLocator();
		this.jiraSoapService = locator.getJirasoapserviceV2(
				new URL("http://localhost:2990/jira/rpc/soap/jirasoapservice-v2"));
		token = jiraSoapService.login("admin", "admin");
	}

	@After
	public void logout() throws java.rmi.RemoteException {
		jiraSoapService.logout(token);
	}

	@Test
	public void multipleDuplicates() throws RemoteValidationException, RemotePermissionException, RemoteAuthenticationException, RemoteException, java.rmi.RemoteException, InterruptedException {
		String summary = "multipleDuplicates" + System.currentTimeMillis();
		RemoteIssue issue1 = createTestIssue(summary);
		Thread.sleep(1000);
		RemoteIssue issue2 = createTestIssue(summary);
		LOG.error(summary);
		assertIssueNumber(summary, 2);
		assertCommentNumber(summary, TimestampPlugin.class.getName(), 1);
		assertTrue(getComments(issue2, TimestampPlugin.class.getName()).size() > 0);
		assertEquals(0, getComments(issue1, TimestampPlugin.class.getName()).size());
	}

	@Test
	public void differentTestCasesForSameBug() throws RemoteException, java.rmi.RemoteException {
		LOG.error("Duplicate entry 213");
		LOG.error("Duplicate entry 312");
		assertIssueNumber("Duplicate entry", 1);
	}

	@Test
	public void logUniqueSummary() throws RemoteException, java.rmi.RemoteException {
		String message = "Tech error nr " + System.currentTimeMillis();
		LOG.error(message);
		assertIssueNumber(message, 1);
		assertCommentNumber(message, MDCPlugin.class.getName(), 1);
	}

	@Test
	public void logDuplicateBySummary() throws RemoteException, java.rmi.RemoteException, InterruptedException {
		String message = "logDuplicateBySummary" + System.currentTimeMillis();
		LOG.error(message);
		Thread.sleep(1000);
		LOG.error(message);
		assertIssueNumber(message, 1);
		assertCommentNumber(message, MDCPlugin.class.getName(), 1);
		assertCommentNumber(message, TimestampPlugin.class.getName(), 2);
	}

	@Test
	public void logDuplicateBySummaryButNotDescription() throws RemoteException, java.rmi.RemoteException {
		String message = "logDuplicateBySummaryButNotDescription" + System.currentTimeMillis();
		LOG.error(message);
		LOG.error(message,  new NullPointerException());
		assertIssueNumber(message, 2);
		assertCommentNumber(message, MDCPlugin.class.getName(), 2);
		assertCommentNumber(message, TimestampPlugin.class.getName(), 2);
	}

	@Test
	public void handlesSpecialCharsInSummary() throws RemoteException, java.rmi.RemoteException {
		String message = "\t!@#$$%^%&*&()_фваыпжäüü" + System.currentTimeMillis();
		LOG.error(message, new NullPointerException());
		assertIssueNumber(message, 1);
	}

	@Test
	public void nestedExceptions() throws RemoteException, java.rmi.RemoteException {

		String innerMessage = "First catch message";
		String message = "Log message" + System.currentTimeMillis();

		try {
			try {
				throw new NullPointerException("Exception message");
			} catch (NullPointerException npe) {
				throw new RuntimeException(innerMessage, npe);
			}
		} catch (RuntimeException e) {
			LOG.error(message, e);
		}

		assertIssueNumber(message, innerMessage, 1);
	}

	private void assertIssueNumber(String summary, String description, int num)
			throws RemoteException, java.rmi.RemoteException {

		String JQL = "summary ~ \"\\\"" + escapeJava(summary);

		if (description != null) {
			JQL += "\\\"\" AND description ~ \"\\\"" + escapeJava(description);
		}

		JQL += "\\\"\"";

		RemoteIssue[] res = jiraSoapService.getIssuesFromJqlSearch(token, JQL, num + 1);
		assertEquals(num, res.length);
	}

	private void assertIssueNumber(String summary, int num) throws RemoteException, java.rmi.RemoteException {
		assertIssueNumber(summary, null, num);
	}

	private List<RemoteComment> getComments(RemoteIssue issue, String comment)
			throws RemotePermissionException, RemoteAuthenticationException,
			RemoteException, java.rmi.RemoteException {

		List<RemoteComment> res = new ArrayList<RemoteComment>();
		for (RemoteComment rComment : jiraSoapService.getComments(token,
				issue.getKey())) {
			if (rComment.getBody().contains(comment)) {
				res.add(rComment);
			}
		}

		return res;
	}

	private void assertCommentNumber(String summary, String comment, int num) throws RemoteException, java.rmi.RemoteException {
		String JQL = "summary ~ \"\\\"" + escapeJava(summary) + "\\\"\"";
		RemoteIssue[] issues = jiraSoapService.getIssuesFromJqlSearch(token, JQL, num + 1);
		int total = 0;
		for (RemoteIssue issue : issues) {
			total += getComments(issue, comment).size();
		}
		assertEquals(num, total);
	}

	private RemoteIssue createTestIssue(String summary)
			throws java.rmi.RemoteException, RemoteValidationException,
			RemotePermissionException, RemoteAuthenticationException,
			RemoteException {
		RemoteIssue issue = new RemoteIssue();
		issue.setSummary(summary);
		issue.setProject("TST");
		issue.setType("1");
		issue = jiraSoapService.createIssue(token, issue);
		return issue;
	}
}
