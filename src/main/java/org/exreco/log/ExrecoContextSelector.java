package org.exreco.log;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class ExrecoContextSelector implements ContextSelector {
	private static final LoggerContext CONTEXT = new LoggerContext("ExrecoDefault");

	@Override
	public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {

		final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
		return ctx != null ? ctx : CONTEXT;
	}

	@Override
	public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
			final URI configLocation) {

		final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
		return ctx != null ? ctx : CONTEXT;
	}

	public LoggerContext locateContext(final String name, final String configLocation) {
		return CONTEXT;
	}

	@Override
	public void removeContext(final LoggerContext context) {
	}

	@Override
	public List<LoggerContext> getLoggerContexts() {
		final List<LoggerContext> list = new ArrayList<LoggerContext>();
		list.add(CONTEXT);
		return Collections.unmodifiableList(list);
	}

	public static LoggerContext getContext() {
		return CONTEXT;
	}

}
