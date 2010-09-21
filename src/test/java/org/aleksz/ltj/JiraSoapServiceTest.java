package org.aleksz.ltj;

import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.junit.Test;

public class JiraSoapServiceTest {

	String baseUrl = "http://jira.atlassian.com/rpc/soap/jirasoapservice-v2";

	@Test
	public void test() throws ServiceException, RemoteAuthenticationException, RemoteException, RemoteException {
		JiraSoapServiceServiceLocator jiraSoapServiceServiceLocator = new JiraSoapServiceServiceLocator();
		JiraSoapService jirasoapservice = jiraSoapServiceServiceLocator.getJirasoapserviceV2();
		String login = jirasoapservice.login("aleksandr.zuikov", "");
		jirasoapservice.logout(login);
	}
}
