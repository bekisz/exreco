package org.exreco.experiment.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.liff.core.DirectReplicationFitnessGene;
import org.exreco.liff.core.Replicator;


public class LiffUtils {
	private static Logger logger = LogManager.getLogger(LiffUtils.class
			.getName());

	public static interface Filter<ElementType> {
		public boolean isIncluded(ElementType element);
	};

	public static class NullFilter<ElementType> implements Filter<ElementType> {
		@Override
		public boolean isIncluded(ElementType replicator) {
			return true;
		}
	};

	public static class DescendantsFilter<ReplicatorType extends Replicator>
			implements Filter<ReplicatorType> {
		final private DirectReplicationFitnessGene commonAncestor;

		public DescendantsFilter(DirectReplicationFitnessGene commonAncestor) {
			this.commonAncestor = commonAncestor;
		}

		@Override
		public boolean isIncluded(ReplicatorType replicator) {
			return replicator.getGene().isDescendantOf(this.commonAncestor);
		}

	};

	public static Object createObject(String className) {
		Object result = null;
		try {
			result = Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			logger.warn("Couldn't instantiate  : " + className, e);

		} catch (IllegalAccessException e) {
			logger.warn("Couldn't access constructor for  : " + className, e);

		} catch (ClassNotFoundException e) {
			logger.warn("Class not found  : " + className, e);

		}
		return result;
	}

	public static <Element> void copyElements(Collection<Element> from,
			Collection<Element> to, Filter<Element> filter) {
		for (Element element : from) {
			if (filter.isIncluded(element)) {
				to.add(element);
			}
		}

	}

	public static String getHostName() {
		String hostname = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();

			// Get IP Address
			// byte[] ipAddr = addr.getAddress();

			// Get hostname
			hostname = addr.getHostName();
		} catch (UnknownHostException e) {
		}
		return hostname;
	}

	public static String getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name;
	}
}
