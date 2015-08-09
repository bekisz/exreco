package org.exreco.logging;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

// note: class name need not match the @Plugin name.
@Plugin(name = "MemoryAppender", category = "Core", elementType = "appender", printObject = true)
public final class MemoryAppender extends AbstractAppender {

	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();
	List<LogEvent> loggedEvents = new LinkedList<LogEvent>();

	protected MemoryAppender(String name, Filter filter, Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	public List<LogEvent> getLoggedEvents() {
		return loggedEvents;
	}

	// The append method is where the appender does the work.
	// Given a log event, you are free to do with it what you want.
	// This example demonstrates:
	// 1. Concurrency: this method may be called by multiple threads
	// concurrently
	// 2. How to use layouts
	// 3. Error handling
	@Override
	public void append(LogEvent event) {
		if (!event.getLevel().isLessSpecificThan(Level.WARN)) {
			readLock.lock();
			try {
				this.getLoggedEvents().add(event);
				final byte[] bytes = getLayout().toByteArray(event);
				System.out.write(bytes);
			} catch (Exception ex) {
				if (!ignoreExceptions()) {
					throw new AppenderLoggingException(ex);
				}
			} finally {
				readLock.unlock();
			}
		}
	}

	// Your custom appender needs to declare a factory method
	// annotated with `@PluginFactory`. Log4j will parse the configuration
	// and call this factory method to construct an appender instance with
	// the configured attributes.
	@PluginFactory
	public static MemoryAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter, @PluginAttribute("otherAttribute") String otherAttribute) {
		if (name == null) {
			LOGGER.error("No name provided for MemoryAppender");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new MemoryAppender(name, filter, layout, true);
	}
}