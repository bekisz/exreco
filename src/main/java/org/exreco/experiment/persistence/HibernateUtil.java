package org.exreco.experiment.persistence;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.RuntimeErrorException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.log.DynamicClassHibernateHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	public static Configuration configuration;
	private static ReadWriteLock rebuildLock = new ReentrantReadWriteLock();
	private static Set<Class<Object>> annotatedDynamicClasses = new HashSet<Class<Object>>();

	private static Logger logger = LogManager.getLogger(HibernateUtil.class.getName());

	synchronized static private void buildSessionFactory() {
		try {
			// Configuration configuration = new Configuration();
			configuration.configure();
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
					.buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			// logger.debug("Hibernate SessionFactory created");
		} catch (Exception ex) {
			// Make sure you log the exception, as it might be swallowed
			logger.error("Hibernate SessionFactory creation failed.", ex);
			// logger.error(ex.getMessage());
			// throw new ExceptionInInitializerError(ex);

		}
	}

	synchronized public static void rebuildSessionFactory() {
		rebuildLock.writeLock().lock();

		try {
			getSessionFactory().close();
			buildSessionFactory();
		} finally {
			rebuildLock.writeLock().unlock();
		}
	}

	synchronized public static void rebuildSessionFactory(Class<Object> dynamicClazz) {
		getAnnotatedDynamicClasses().add(dynamicClazz);
		try {
			rebuildLock.writeLock().lock();
			getSessionFactory().close();
			HibernateUtil.configuration = new Configuration();
			for (Class<Object> annotedDynamicClazz : getAnnotatedDynamicClasses()) {
				HibernateUtil.getConfiguration().addAnnotatedClass(annotedDynamicClazz);
			}

			buildSessionFactory();
		} finally {
			rebuildLock.writeLock().unlock();
		}

	}

	public static SessionFactory getSessionFactory() {
		if (configuration == null) {
			configuration = new Configuration();
			buildSessionFactory();
		}
		return sessionFactory;
	}

	synchronized public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	static public void persist(Object object) {

		HibernateUtil.rebuildLock.readLock().lock();
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();

			session.persist(object);
			session.getTransaction().commit();
			session.close();
		} finally {
			HibernateUtil.rebuildLock.readLock().unlock();
		}
	}

	static public void saveOrUpdate(Object object) {

		HibernateUtil.rebuildLock.readLock().lock();
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();

			session.saveOrUpdate(object);
			session.getTransaction().commit();
			session.close();
		} finally {
			HibernateUtil.rebuildLock.readLock().unlock();
		}

	}

	public static ReadWriteLock getRebuildLock() {
		return rebuildLock;
	}

	public static Set<Class<Object>> getAnnotatedDynamicClasses() {
		return annotatedDynamicClasses;
	}

}
