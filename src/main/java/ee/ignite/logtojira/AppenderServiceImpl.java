package ee.ignite.logtojira;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.rmi.RemoteException;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.spi.LoggingEvent;

import ee.ignite.logtojira.plugin.Plugin;
import ee.ignite.logtojira.soap.JiraSoapService;
import ee.ignite.logtojira.soap.RemoteComment;
import ee.ignite.logtojira.soap.RemoteIssue;

public class AppenderServiceImpl implements AppenderService {

	private Config config;
	private final JiraSoapService jiraService;

	public AppenderServiceImpl(Config config, JiraSoapService jiraService) {
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
		result.setEnvironment(composeEnvironmentDescription());

		return result;
	}

	private String composeDescription(LoggingEvent loggingEvent) {

		StringBuilder result = new StringBuilder();

		if (loggingEvent.getThrowableInformation() != null) {
			result.append(Util.toString(loggingEvent.getThrowableInformation().getThrowable()));
		}

		return result.toString();
	}

	private String composeEnvironmentDescription() {

		StringBuilder result = new StringBuilder();

		for (Entry<String, String> e : System.getenv().entrySet()) {
			result.append(e.getKey());
			result.append("=");
			result.append(e.getValue());
			result.append("; ");
		}

		result.append("\n\n");

		for (Entry<Object, Object> e : System.getProperties().entrySet()) {
			result.append(e.getKey());
			result.append("=");
			result.append(e.getValue());
			result.append("; ");
		}

		return result.toString();
	}


	@Override
	public RemoteIssue getLatestDuplicate(RemoteIssue issue, String token) throws RemoteException,
			RemoteException {

		StringBuilder JQL = new StringBuilder();
		JQL.append("project = ");
		JQL.append(config.getProject());
		JQL.append(" AND summary ~ \"\\\"");
		JQL.append(escapeJava(issue.getSummary()));
		JQL.append("\\\"\" AND description ");
		if (StringUtils.isBlank(issue.getDescription())) {
			JQL.append("IS EMPTY");
		} else {
			JQL.append("~ \"\\\"");
			JQL.append(escapeJava(issue.getDescription()));
			JQL.append("\\\"\"");
		}
		JQL.append(" AND status in (Open, \"In Progress\", Reopened)");
		JQL.append(" ORDER BY created");

		RemoteIssue[] duplicates = jiraService.getIssuesFromJqlSearch(token, JQL.toString(), 1);
		return duplicates.length > 0 ? duplicates[0] : null;
	}

	@Override
	public boolean duplicateExists(RemoteIssue issue, String token) throws RemoteException, RemoteException {
		return getLatestDuplicate(issue, token) != null;
	}

	@Override
	public RemoteComment createComment(Plugin plugin, LoggingEvent loggingEvent) {
		RemoteComment comment = new RemoteComment();
		comment.setBody(plugin.getText(loggingEvent));
		return comment;
	}

	@Override
	public boolean duplicateExists(RemoteComment comment, RemoteIssue issue,
			String token) throws RemoteException, RemoteException {

		StringBuilder JQL = new StringBuilder();
		JQL.append("key = ");
		JQL.append(issue.getKey());
		JQL.append(" AND comment ~ \"\\\"");
		JQL.append(escapeJava(comment.getBody()));
		JQL.append("\\\"\"");

		return jiraService.getIssuesFromJqlSearch(token, JQL.toString(), 1).length > 0;
	}

}
