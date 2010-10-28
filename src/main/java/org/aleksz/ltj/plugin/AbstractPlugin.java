package org.aleksz.ltj.plugin;

import org.apache.log4j.spi.LoggingEvent;

public abstract class AbstractPlugin implements Plugin {

	@Override
	public String getText(LoggingEvent loggingEvent) {
		return getClass().getName();
	}

}
