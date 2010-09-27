package org.aleksz.ltj;

import org.apache.log4j.Logger;
import org.junit.Test;


public class TestAppender {

	private static final Logger LOG = Logger.getLogger(TestAppender.class);

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
