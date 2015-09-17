package org.exreco.log;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.*;

import org.apache.logging.log4j.core.layout.PatternLayout;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LiffEventListener;

// note: class name need not match the @Plugin name.
@Plugin(name = "DistributedAppender", category = "Core", elementType = "appender", printObject = true)
public final class DistributedAppender extends AbstractAppender {
	private EventTopicHome eventTopicHome;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6491086248539841870L;
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();
	List<LogEvent> bufferedEvents = new LinkedList<LogEvent>();
	private LiffEventListener log4j2EventListener;
	public static final String DEFAULT_TOPIC_NAME = "Log4j2Events";
	private String topicName = DEFAULT_TOPIC_NAME;
	private static Boolean isDistributedLoggingInited = new Boolean(false);
	public DistributedAppender() {
		super("aDistributedAppender", null, null);
	}

	protected DistributedAppender(String name, Filter filter, Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	public List<LogEvent> getBufferedEvents() {
		return bufferedEvents;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public LiffEventListener getLog4j2EventListener() {
		return log4j2EventListener;
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

		readLock.lock();

		try {
			if (eventTopicHome != null && log4j2EventListener == null) {
				this.log4j2EventListener = eventTopicHome.getEventListener(this.getTopicName());
			}
			if (eventTopicHome == null) {
				this.getBufferedEvents().add(event);
				// System.err.println("Event added to exreco appender buffer : "
				// + event.toString());

			} else {
				if (this.getBufferedEvents().size() > 0) {
					for (Iterator<LogEvent> iterator = bufferedEvents.iterator(); iterator.hasNext();) {
						LogEvent logEvent = iterator.next();
						this.log4j2EventListener.eventOccurred(logEvent);
						// System.err.println("Sending buffered event : " +
						// logEvent.toString());
					}
					this.getBufferedEvents().clear();
				} else {
					this.log4j2EventListener.eventOccurred(event);
					// System.err.println("Sending event : " +
					// event.toString());
				}
			}

		} catch (Exception ex) {
			if (!ignoreExceptions()) {
				throw new AppenderLoggingException(ex);
			}
		} finally {
			readLock.unlock();
		}

	}

	// Your custom appender needs to declare a factory method
	// annotated with `@PluginFactory`. Log4j will parse the configuration
	// and call this factory method to construct an appender instance with
	// the configured attributes.
	@PluginFactory
	public static DistributedAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter, @PluginAttribute("otherAttribute") String otherAttribute) {
		if (name == null) {
			LOGGER.error("No name provided for DistributedAppender");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new DistributedAppender(name, filter, layout, true);
	}

	public static Configuration addAppender2Configuration() {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

		final Configuration config = ctx.getConfiguration();
		config.getAppenders().clear();
		DistributedAppender appender = DistributedAppender.createAppender("distributedAppender", null, null, null);
		appender.start();
		config.addAppender(appender);
		AppenderRef ref = AppenderRef.createAppenderRef("distributedAppender", Level.DEBUG, null);
		AppenderRef[] refs = new AppenderRef[] { ref };
		LoggerConfig loggerConfig = LoggerConfig.createLogger("true", Level.DEBUG, LogManager.ROOT_LOGGER_NAME, "true",
				refs, null, config, null);
		loggerConfig.addAppender(appender, Level.INFO, null);
		config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
		ctx.updateLoggers();
		return config;
	}
	public static void inject2LoggerContext(String log4j2ConfigLocation, EventTopicHome eventTopicHome ) throws URISyntaxException {
		// LogManager.getContext().

		// LoggerContextFactory oldLoggerContextFactory =
		// LogManager.getFactory();
		synchronized (isDistributedLoggingInited) {

			if (!isDistributedLoggingInited) {
				// !(oldLoggerContextFactory instanceof
				// ExrecoLog4jContextFactory)
				LoggerContext loggerContext = (LoggerContext) LogManager.getContext();
				/*
				 * ExrecoContextSelector contextSelector = new
				 * ExrecoContextSelector(); URI log4j2ConfigLocationURI = new
				 * URI(getLog4j2ConfigLocation());
				 * ExrecoContextSelector.getContext().setConfigLocation(
				 * log4j2ConfigLocationURI); LoggerContextFactory
				 * newLoggerContextFactory = new
				 * ExrecoLog4jContextFactory(contextSelector);
				 * LogManager.setFactory(newLoggerContextFactory);
				 */
				// new JmsEventTopicHome();

				// Configuration configuration =
				// DistributedAppender.addAppender2Configuration();
				// logger = LogManager.getLogger(this.getClass());
				// oldLoggerContext.updateLoggers(configuration);
				// oldLoggerContext.reconfigure();

				URI log4j2ConfigLocationURI = new URI(log4j2ConfigLocation);
				loggerContext.setConfigLocation(log4j2ConfigLocationURI);
				Configuration config = loggerContext.getConfiguration();
				// config.getAppenders().clear();

				DistributedAppender appender = DistributedAppender.createAppender("distributedAppender", null, null,
						null);
				appender.setEventTopicHome(eventTopicHome);
				appender.start();
				config.addAppender(appender);
				LoggerConfig rootLoggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
				// AppenderRef ref =
				// AppenderRef.createAppenderRef("distributedAppender",
				// Level.DEBUG, null);
				// AppenderRef[] refs = new AppenderRef[] { ref };
				// LoggerConfig loggerConfig = LoggerConfig.createLogger("true",
				// Level.DEBUG, LogManager.ROOT_LOGGER_NAME, "true",
				// refs, null, config, null);
				// loggerConfig.addAppender(appender, Level.DEBUG, null);
				rootLoggerConfig.addAppender(appender, Level.DEBUG, null);
				// config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
				loggerContext.updateLoggers();
		
				isDistributedLoggingInited = true;
			}
		}

	}

	public EventTopicHome getEventTopicHome() {
		return eventTopicHome;
	}

	public void setEventTopicHome(EventTopicHome eventTopicHome) {
		this.eventTopicHome = eventTopicHome;
	}
}