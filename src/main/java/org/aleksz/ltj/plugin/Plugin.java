package org.aleksz.ltj.plugin;

import org.apache.log4j.spi.LoggingEvent;

public interface Plugin {

	String getText(LoggingEvent loggingEvent);

}
