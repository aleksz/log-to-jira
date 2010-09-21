package org.aleksz.ltj;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.junit.Test;

public class JiraSoapServiceTest {

	@Test
	public void test() throws ServiceException, RemoteAuthenticationException, RemoteException, RemoteException, MalformedURLException {
		URL jira = new URL("http://localhost:2990/jira/rpc/soap/jirasoapservice-v2");
		JiraSoapServiceServiceLocator jiraSoapServiceServiceLocator = new JiraSoapServiceServiceLocator();
		JiraSoapService jirasoapservice = jiraSoapServiceServiceLocator.getJirasoapserviceV2(jira);
		String login = jirasoapservice.login("admin", "admin");
		jirasoapservice.logout(login);
	}
}
