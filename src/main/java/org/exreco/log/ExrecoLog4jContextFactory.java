package org.exreco.log;

import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.BasicContextSelector;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class ExrecoLog4jContextFactory extends Log4jContextFactory {

	public ExrecoLog4jContextFactory() {
		super(new BasicContextSelector());
	}

	public ExrecoLog4jContextFactory(ContextSelector selector) {
		super(selector);
	
	}

}
