/**
 * Copyright 2010 Ignite OÜ (www.ignite.ee)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package ee.ignite.logtojira;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.rmi.RemoteException;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import ee.ignite.logtojira.AppenderService;
import ee.ignite.logtojira.LogToJiraAppender;
import ee.ignite.logtojira.plugin.Plugin;
import ee.ignite.logtojira.soap.JiraSoapService;
import ee.ignite.logtojira.soap.RemoteAuthenticationException;
import ee.ignite.logtojira.soap.RemoteComment;
import ee.ignite.logtojira.soap.RemoteIssue;


public class LogToJiraAppenderTest {

	private static final String TOKEN = "sdfagsdf";
	private static final String USERNAME = "theUser";
	private static final String PASS = "thePass";

	private LogToJiraAppender appender;
	private AppenderService appenderService;
	private JiraSoapService jiraService;
	private Plugin plugin1;
	private Plugin plugin2;

	@Before
	public void init() {

		plugin1 = new Plugin() {
			@Override
			public String getText(LoggingEvent loggingEvent) {
				return "1";
			}
		};

		plugin2 = new Plugin() {
			@Override
			public String getText(LoggingEvent loggingEvent) {
				return "1";
			}
		};

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
		appender.setPlugins(Arrays.asList(plugin1, plugin2));
	}

	@Test
	public void abortIfNotEnabled() {
		appender.setEnabled(false);
		replay(jiraService, appenderService);
		appender.append(null);
		verify(jiraService, appenderService);
	}

	@Test
	public void append() throws RemoteAuthenticationException, RemoteException, RemoteException {
		LoggingEvent logEvent = createTestLoggingEvent();
		RemoteIssue issue = new RemoteIssue();
		RemoteComment comment1 = new RemoteComment();
		RemoteComment comment2 = new RemoteComment();

		expect(jiraService.login(USERNAME, PASS)).andReturn(TOKEN);
		expect(appenderService.createIssue(logEvent)).andReturn(issue);
		expect(appenderService.getLatestDuplicate(issue, TOKEN)).andReturn(null);
		expect(jiraService.createIssue(TOKEN, issue)).andReturn(issue);
		expect(appenderService.createComment(plugin1, logEvent)).andReturn(comment1);
		expect(appenderService.duplicateExists(comment1, issue, TOKEN)).andReturn(false);
		jiraService.addComment(TOKEN, issue.getKey(), comment1);
		expect(appenderService.createComment(plugin2, logEvent)).andReturn(comment2);
		expect(appenderService.duplicateExists(comment1, issue, TOKEN)).andReturn(true);
		expect(jiraService.logout(TOKEN)).andReturn(true);
		replay(jiraService, appenderService);

		appender.append(logEvent);

		verify(jiraService, appenderService);
	}

	@Test
	public void appendDuplicate() throws RemoteAuthenticationException, ee.ignite.logtojira.soap.RemoteException, RemoteException {
		LoggingEvent logEvent = createTestLoggingEvent();
		RemoteIssue issue = new RemoteIssue();
		RemoteIssue duplicate = new RemoteIssue();
		RemoteComment comment = new RemoteComment();

		expect(jiraService.login(USERNAME, PASS)).andReturn(TOKEN);
		expect(appenderService.createIssue(logEvent)).andReturn(issue);
		expect(appenderService.getLatestDuplicate(issue, TOKEN)).andReturn(duplicate);
		expect(appenderService.createComment(plugin1, logEvent)).andReturn(comment);
		expect(appenderService.duplicateExists(comment, duplicate, TOKEN)).andReturn(false);
		jiraService.addComment(TOKEN, duplicate.getKey(), comment);
		expect(appenderService.createComment(plugin2, logEvent)).andReturn(comment);
		expect(appenderService.duplicateExists(comment, duplicate, TOKEN)).andReturn(false);
		jiraService.addComment(TOKEN, duplicate.getKey(), comment);
		expect(jiraService.logout(TOKEN)).andReturn(true);
		replay(jiraService, appenderService);

		appender.append(logEvent);

		verify(jiraService, appenderService);
	}

	private LoggingEvent createTestLoggingEvent() {
		Logger log = Logger.getLogger(LogToJiraAppenderTest.class);
		return new LoggingEvent(null, log, Level.ERROR, "tstmsg", null);
	}
}
