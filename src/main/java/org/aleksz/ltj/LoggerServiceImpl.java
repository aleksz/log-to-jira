package org.aleksz.ltj;

import java.rmi.RemoteException;

import org.apache.log4j.spi.LoggingEvent;

public class LoggerServiceImpl implements LoggerService {

	private Config config;
	private final JiraSoapService jiraService;

	public LoggerServiceImpl(Config config, JiraSoapService jiraService) {
		this.config = config;
		this.jiraService = jiraService;
	}

	@Override
	public RemoteIssue createIssue(LoggingEvent loggingEvent) {
		RemoteIssue result = new RemoteIssue();

		result.setProject(config.getProject());
		result.setType(config.getIssueTypeId());
		result.setSummary(loggingEvent.getRenderedMessage());
		result.setDescription(composeDescription(loggingEvent));

		return result;
	}

	private String composeDescription(LoggingEvent loggingEvent) {

		StringBuilder result = new StringBuilder();

		for (String line : loggingEvent.getThrowableStrRep()) {
			result.append(line);
			result.append("\n");
		}

		return result.toString();
	}

	@Override
	public boolean duplicateExists(RemoteIssue issue, String token) throws RemoteException, RemoteException {
		String JQL =
			"project = " + config.getProject() +
			" AND summary ~ \"\\\"" + issue.getSummary() + "\\\"\" " +
			" AND status in (Open, \"In Progress\", Reopened)";
		return jiraService.getIssuesFromJqlSearch(token, JQL, 1).length > 0;
	}
}
