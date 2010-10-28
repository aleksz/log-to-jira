package org.aleksz.ltj.plugin;

import java.util.Date;

import org.apache.log4j.spi.LoggingEvent;

public class TimestampPlugin extends AbstractPlugin {

	@Override
	public String getText(LoggingEvent loggingEvent) {
		return super.getText(loggingEvent) + ": " + new Date();
	}

}
