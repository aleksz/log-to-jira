package ee.ignite.logtojira;

import java.rmi.RemoteException;

import org.apache.log4j.spi.LoggingEvent;

import ee.ignite.logtojira.plugin.Plugin;
import ee.ignite.logtojira.soap.RemoteComment;
import ee.ignite.logtojira.soap.RemoteIssue;

public interface AppenderService {

	RemoteIssue createIssue(LoggingEvent loggingEvent) throws RemoteException, RemoteException;

	RemoteComment createComment(Plugin plugin, LoggingEvent loggingEvent);

	RemoteIssue getLatestDuplicate(RemoteIssue issue, String token) throws RemoteException, RemoteException;

	boolean duplicateExists(RemoteIssue issue, String token) throws RemoteException, RemoteException;

	boolean duplicateExists(RemoteComment comment, RemoteIssue issue, String token) throws RemoteException, RemoteException;
}
