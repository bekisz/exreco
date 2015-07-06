package org.exreco.experiment.log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class SqlTable implements Table, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2388772942482849615L;
	private static Logger logger = LogManager.getLogger(SqlTable.class
			.getName());
	private static final Marker SQL_MARKER = MarkerManager.getMarker("SQL");
	@SuppressWarnings("unused")
	private static final Marker SQL_UPDATE_MARKER = MarkerManager.getMarker(
			"SQL_UPDATE", SQL_MARKER);
	@SuppressWarnings("unused")
	private static final Marker SQL_QUERY_MARKER = MarkerManager.getMarker(
			"SQL_QUERY", SQL_MARKER);
	private Map<String, Class<?>> header;
	private String name;
	SqlSession sqlSession;
	private Class lineageClass = null;
	private PreparedStatement preparedInsertRow = null;
	private int rowNumber = 0;
	private boolean isRewrite = false;
	private Session session;

	public Session getSession() {
		return session;
	}

	/**
	 * @return the preparedInsertRow
	 */
	public PreparedStatement getPreparedInsertRow() {
		return preparedInsertRow;
	}

	public SqlTable() {
	}

	@Override
	public void init(org.exreco.experiment.log.Session session, String tableName)
			throws Exception {
		this.setSqlSession((SqlSession) session);

		this.init(tableName);

	}

	@Override
	public void init(org.exreco.experiment.log.Session session,
			String tableName, boolean rewrite) throws Exception {
		this.isRewrite = rewrite;
		this.init(session, tableName);

	}

	@SuppressWarnings("unused")
	private void initPreparedInsertRow() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO ");
		sb.append(this.getName());
		sb.append(" (");
		char vesszo = ' ';
		for (String key : this.header.keySet()) {
			sb.append(vesszo);
			sb.append(key.replace('-', '_').replace('.', '_'));
			vesszo = ',';
		}
		sb.append(") ");

		sb.append(" VALUES (");
		vesszo = ' ';

		for (String key : this.header.keySet()) {
			sb.append(vesszo);

			sb.append('?');
			vesszo = ',';
		}
		sb.append(')');

		this.preparedInsertRow = this.getSqlSession().getConnection()
				.prepareStatement(sb.toString());
	}

	private void init(String tableName) throws Exception {
		this.setName(tableName);
		if (this.isRewrite()) {
			Statement s = this.getSqlSession().getConnection()
					.createStatement();
			String sqlSttring = "DROP TABLE IF EXISTS " + this.getName();
			s.executeUpdate(sqlSttring);
			// logger.debug(SQL_UPDATE_MARKER, sqlSttring);
			logger.debug(SQL_MARKER, sqlSttring);
			s.close();
		}
		// this.createTable();
		// this.getSqlSession().getConnection().setAutoCommit(false);

	}

	private String java2SqlType(Class<?> javaType) {
		String sqlType = "??";
		if (javaType.getSimpleName().equalsIgnoreCase("String")) {
			sqlType = "CHAR(80)";
		} else if (javaType.getSimpleName().equalsIgnoreCase("Double")) {
			sqlType = "DOUBLE";

		} else if (javaType.getSimpleName().equalsIgnoreCase("Integer")) {
			sqlType = "INT";
		} else if (javaType.getSimpleName().equalsIgnoreCase("Long")) {
			sqlType = "BIGINT";
		} else if (javaType.getSimpleName().equalsIgnoreCase("Boolean")) {
			sqlType = "BOOLEAN";
		}

		return sqlType;
	}

	private void createTable() throws Exception {
		// logger.debug(SQL_UPDATE_MARKER, "CREATE TABLE {}", this.getName());

		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(this.getName());
		sb.append(" (");
		sb.append("id INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), ");
		char vesszo = ' ';
		for (String key : this.getHeader().keySet()) {
			sb.append(vesszo);
			sb.append(key.replace('-', '_').replace('.', '_'));
			sb.append(' ');
			sb.append(this.java2SqlType(this.getHeader().get(key)));
			vesszo = ',';
		}
		sb.append(") ");
		Statement s = this.getSqlSession().getConnection().createStatement();

		s.executeUpdate(sb.toString());
		// logger.debug(SQL_UPDATE_MARKER, s.toString());
		logger.debug(SQL_MARKER, s.toString());
		s.close();
	}

	private void createHeader(final Map<String, ? super Object> row) {

		this.header = new LinkedHashMap<String, Class<?>>(row.size());
		for (String key : row.keySet()) {
			Class<?> cls = row.get(key).getClass();
			this.getHeader().put(key, cls);
		}

	}

	@Override
	synchronized public void insertRow(final Map<String, ? super Object> row)
			throws Exception {

		if (this.rowNumber == 0 && this.isRewrite()) {
			this.createHeader(row);
			this.createTable();
			this.initPreparedInsertRow();
		} else if (this.rowNumber == 0) {
			this.createHeader(row);
			this.initPreparedInsertRow();
		}
		this.rowNumber++;

		int i = 1;

		for (String key : row.keySet()) {
			Object object = row.get(key);

			if (object instanceof Double && Double.isNaN((Double) object)) {
				this.getPreparedInsertRow().setNull(i, java.sql.Types.DOUBLE);
			} else if (object instanceof Double
					&& Double.isInfinite((Double) object)) {
				logger.error(
						"Infinite double received as value to store in DB for key {}. Setting value to null.",
						key);
				this.getPreparedInsertRow().setNull(i, java.sql.Types.DOUBLE);
			}

			else {
				this.getPreparedInsertRow().setObject(i, object);
			}

			i++;
		}
		// logger.debug(SQL_UPDATE_MARKER,
		// this.getPreparedInsertRow().toString());
		logger.debug(SQL_MARKER, this.getPreparedInsertRow().toString());
		this.getPreparedInsertRow().executeUpdate();
		// this.getPreparedInsertRow().close();

		// -------------
		// TODO : Fix it . It is only a test

		/*
		 * session.beginTransaction(); CaseDao stock = new CaseDao();
		 * 
		 * stock.setThreadName("thread1" + logMap.get("population").toString());
		 * 
		 * session.save(stock); session.getTransaction().commit();
		 * session.close();
		 */
		// ---

		if (this.getLineageClass() == null) {
			Class lineageClass = this.createLineageClass(row);
			this.setLineageClass(lineageClass);
			SessionFactory sessionFactory = new AnnotationConfiguration()
					.addPackage("org.exreco.experiment.persistence") // the
																		// fully
																		// qualified
																		// package
																		// name
					.addAnnotatedClass(this.getLineageClass())

					.configure().buildSessionFactory();
			this.session = sessionFactory.openSession();
		}
		Object person = this.getLineageClass().newInstance();
		for (String key : row.keySet()) {
			Field field = this.getLineageClass().getDeclaredField(key.replace('-', '_'));
			field.set(person, row.get(key));
		}
		

		this.getSession().beginTransaction();

		this.getSession().persist(person);
		session.getTransaction().commit();
		// -- test ended

	}

	private Class<Object> createLineageClass(Map<String, ? super Object> map)
			throws Exception {
		String lineageClassName = "org.exreco.experiment.persistence.Lineage";

		ClassPool cp = ClassPool.getDefault();
		CtClass ctClass = null;
		ctClass = cp.makeClass(lineageClassName);

		for (String key : map.keySet()) {
			CtField nameField = new CtField(cp.get(map.get(key).getClass().getName()),
					key.replace('-', '_'), ctClass);
			nameField.setModifiers(Modifier.PUBLIC);
			ctClass.addField(nameField);
		}

		ClassFile classFile = ctClass.getClassFile();

		AnnotationsAttribute attr = new AnnotationsAttribute(
				classFile.getConstPool(), AnnotationsAttribute.visibleTag);
		Annotation entityAnnotation = new Annotation(Entity.class.getName(),
				classFile.getConstPool());
		attr.addAnnotation(entityAnnotation);
		classFile.addAttribute(attr);
		AnnotationsAttribute attribute = new AnnotationsAttribute(
				classFile.getConstPool(), AnnotationsAttribute.visibleTag);

		Annotation idAnnotation = new Annotation(Id.class.getName(),
				classFile.getConstPool());
		attribute.addAnnotation(idAnnotation);

		Annotation gvAnnotation = new Annotation(
				GeneratedValue.class.getName(), classFile.getConstPool());
		attribute.addAnnotation(gvAnnotation);
		CtField idField = new CtField(cp.get(Long.class.getName()), "id",
				ctClass);
		idField.getFieldInfo().addAttribute(attribute);

		ctClass.addField(idField);
		return ctClass.toClass();

	}

	@Override
	public void close() {
		this.getSession().close();
	}

	@Override
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	/**
	 * @return the header
	 */
	public Map<String, Class<?>> getHeader() {
		return header;
	}

	/**
	 * @return the isRewrite
	 */
	public boolean isRewrite() {
		return isRewrite;
	}

	/**
	 * @param isRewrite
	 *            the isRewrite to set
	 */
	public void setRewrite(boolean isRewrite) {
		this.isRewrite = isRewrite;
	}

	public Class getLineageClass() {
		return lineageClass;
	}

	public void setLineageClass(Class lineageClass) {
		this.lineageClass = lineageClass;
	}

}
