package ee.ignite.logtojira.plugin;

import org.apache.log4j.spi.LoggingEvent;

public class RuntimePlugin extends AbstractPlugin {

	@Override
	public String getText(LoggingEvent loggingEvent) {
		StringBuilder res = new StringBuilder(super.getText(loggingEvent));
		res.append(":\n");
		res.append("Available processors: ");
		res.append(Runtime.getRuntime().availableProcessors());
		res.append("\nFree memory: ");
		res.append(Runtime.getRuntime().freeMemory());
		res.append("\nMax memory: ");
		res.append(Runtime.getRuntime().maxMemory());
		res.append("\nTotal memory: ");
		res.append(Runtime.getRuntime().totalMemory());
		return  res.toString();
	}

}
