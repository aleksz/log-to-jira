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
