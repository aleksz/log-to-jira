package org.aleksz.ltj;

import java.rmi.RemoteException;

import org.aleksz.ltj.plugin.Plugin;
import org.aleksz.ltj.soap.RemoteComment;
import org.aleksz.ltj.soap.RemoteIssue;
import org.apache.log4j.spi.LoggingEvent;

public interface AppenderService {

	RemoteIssue createIssue(LoggingEvent loggingEvent) throws RemoteException, RemoteException;

	RemoteComment createComment(Plugin plugin, LoggingEvent loggingEvent);

	RemoteIssue getLatestDuplicate(RemoteIssue issue, String token) throws RemoteException, RemoteException;

	boolean duplicateExists(RemoteIssue issue, String token) throws RemoteException, RemoteException;

	boolean duplicateExists(RemoteComment comment, RemoteIssue issue, String token) throws RemoteException, RemoteException;
}
