package org.aleksz.ltj;

import java.rmi.RemoteException;

import org.aleksz.ltj.soap.RemoteIssue;
import org.apache.log4j.spi.LoggingEvent;

public interface AppenderService {

	RemoteIssue createIssue(LoggingEvent loggingEvent) throws RemoteException, RemoteException;

	boolean duplicateExists(RemoteIssue issue, String token) throws RemoteException, RemoteException;

}
