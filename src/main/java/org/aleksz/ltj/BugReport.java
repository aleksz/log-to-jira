package org.aleksz.ltj;

import java.util.Map.Entry;

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

		result.append("\n");

		for (Entry<Object, Object> e : System.getProperties().entrySet()) {
			result.append(e.getKey());
			result.append(" = ");
			result.append(e.getValue());
			result.append("\n");
		}

		return result.toString();
	}
}
