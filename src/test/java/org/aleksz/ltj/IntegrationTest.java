package org.aleksz.ltj;

import static junit.framework.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.aleksz.ltj.soap.JiraSoapService;
import org.aleksz.ltj.soap.JiraSoapServiceServiceLocator;
import org.aleksz.ltj.soap.RemoteAuthenticationException;
import org.aleksz.ltj.soap.RemoteException;
import org.aleksz.ltj.soap.RemoteIssue;
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
	public void logUniqueSummary() throws RemoteException, java.rmi.RemoteException {
		String message = "Tech error nr " + System.currentTimeMillis();
		LOG.error(message);
		String JQL = "summary ~ \"\\\"" + message + "\\\"\"";
		RemoteIssue[] result = jiraSoapService.getIssuesFromJqlSearch(token, JQL, 2);
		assertEquals(1, result.length);
	}

	@Test
	public void logDuplicateSummary() throws RemoteException, java.rmi.RemoteException {
		String message = "This is error message";
		LOG.error(message);
		LOG.error(message);
		String JQL = "summary ~ \"\\\"" + message + "\\\"\"";
		RemoteIssue[] result = jiraSoapService.getIssuesFromJqlSearch(token, JQL, 2);
		assertEquals(1, result.length);
	}

	@Test
	public void logException() {
		try {
			throw new NullPointerException("Exception message");
		} catch (NullPointerException npe) {
			LOG.error("Log message", npe);
		}
	}

	@Test
	public void nestedExceptions() {
		try {
			try {
				throw new NullPointerException("Exception message");
			} catch (NullPointerException npe) {
				throw new RuntimeException("First catch message", npe);
			}
		} catch (RuntimeException e) {
			LOG.error("Log message", e);
		}
	}
}
