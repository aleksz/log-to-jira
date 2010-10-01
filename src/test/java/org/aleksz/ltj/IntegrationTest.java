package org.aleksz.ltj;

import org.apache.log4j.Logger;
import org.junit.Test;


public class IntegrationTest {

	private static final Logger LOG = Logger.getLogger(IntegrationTest.class);

	@Test
	public void logUniqueException() {
		LOG.error("Tech error nr " + System.currentTimeMillis());
	}

	@Test
	public void logErrorMessage() {
		LOG.error("This is error message");
	}

	@Test
	public void logException() {
		try {
			throw new NullPointerException("Exception message");
		} catch (NullPointerException npe) {
			LOG.error("Log message", npe);
		}
	}

	@Test
	public void nestedExceptions() {
		try {
			try {
				throw new NullPointerException("Exception message");
			} catch (NullPointerException npe) {
				throw new RuntimeException("First catch message", npe);
			}
		} catch (RuntimeException e) {
			LOG.error("Log message", e);
		}
	}
}
