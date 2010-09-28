package org.aleksz.ltj;

import org.apache.log4j.spi.LoggingEvent;

public class BugReport {

	private Config config;

	public BugReport(Config config) {
		this.config = config;
	}

	public RemoteIssue getIssue(LoggingEvent loggingEvent) {
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
}
