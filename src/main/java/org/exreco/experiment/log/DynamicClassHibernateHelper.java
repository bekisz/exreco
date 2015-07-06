package org.exreco.experiment.log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.persistence.DynamicClass;
import org.exreco.experiment.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class DynamicClassHibernateHelper implements Serializable {

	private static final long serialVersionUID = 7049643320977423988L;
	private String superClassName = "org.exreco.experiment.persistence.DynamicClass";
	

	private Class<Object> dynamicClass;
	private boolean idFieldNeeded = true;
	private boolean isEmbeddable = false;
	private static Logger logger = LogManager
			.getLogger(DynamicClassHibernateHelper.class.getName());

	private String dynamicClassName;

	public DynamicClassHibernateHelper(String dynamicClassName) {
		super();
		this.dynamicClassName = dynamicClassName;
	}
	public DynamicClassHibernateHelper(String dynamicClassName, String superClassName) {
		super();
		this.dynamicClassName = dynamicClassName;
		this.superClassName = superClassName;
	}
	public Class<Object> getDynamicClass() {
		return dynamicClass;
	}

	public void setDynamicClass(Class<Object> dynamicClass) {
		this.dynamicClass = dynamicClass;
	}

	@SuppressWarnings("unchecked")
	synchronized protected Class<Object> loadDynamicClass() {
		Class<Object> dynamicClazz = null;

		try {
			dynamicClazz = (Class<Object>) Class.forName(this.dynamicClassName,
					false, this.getClass().getClassLoader());
			logger.debug("Dynamic Annotated class " + this.dynamicClassName
					+ " found.");
		} catch (ClassNotFoundException e) {
			logger.debug("Dynamic Annotated class " + this.dynamicClassName
					+ " *not* found.");
		}
		return dynamicClazz;
	}
	synchronized public Object createDynamicObject(final Map<String, ? super Object> attributesMap) throws Exception {
		// ---

		Class<Object> dynamicClazz = this.loadDynamicClass();

		if (dynamicClazz == null) {
			dynamicClazz = this.createDynamicClass(attributesMap);
		
			//HibernateUtil.rebuildSessionFactory();
			HibernateUtil.rebuildSessionFactory(dynamicClazz);

		}
		this.setDynamicClass(dynamicClazz);
		Object dynamicObject = this.getDynamicClass().newInstance();

		this.fillObjectAttributesFromMap(dynamicObject, attributesMap);	
		return dynamicObject;
	}
	synchronized public void persist(
			final Map<String, ? super Object> attributesMap) throws Exception {

		Object dynamicObject = this.createDynamicObject(attributesMap);
		HibernateUtil.persist(dynamicObject);

	}

	synchronized public void insertRow(
			final Map<String, ? super Object> attributesMap) throws Exception {
		this.persist(attributesMap);

	}

	synchronized protected Object convertMapValue2FieldValue(String key, Object value) {
		return value;
	}

	synchronized protected Object fillObjectAttributesFromMap(Object object,
			Map<String, ? super Object> map) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

		for (String key : map.keySet()) {
			Field field = object.getClass().getDeclaredField(
					key.replace('-', '_').replace('.', '_'));

			Object oldValue = map.get(key);
			Object newValue = this.convertMapValue2FieldValue(key, oldValue);
			field.set(object, newValue);
		}
		return object;
	}

	protected void addIdField(CtClass ctClass) throws CannotCompileException,
			NotFoundException {
		ClassPool cp = ClassPool.getDefault();
		ClassFile classFile = ctClass.getClassFile();

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

		this.addField(ctClass, idField);

	}

	protected void addField(CtClass ctClass, CtField nameField)
			throws CannotCompileException {
		ctClass.addField(nameField);

	}

	protected Class<Object> createDynamicClass(
			Map<String, ? super Object> attributesMap) throws Exception {
		ClassPool cp = ClassPool.getDefault();
		CtClass ctClass = cp.makeClass(dynamicClassName);
		CtClass superCtClass = cp.get(this.superClassName);
		
		ctClass.setSuperclass(superCtClass);
		for (String key : attributesMap.keySet()) {
			CtField nameField = new CtField(cp.get(attributesMap.get(key)
					.getClass().getName()), key.replace('-', '_').replace('.',
					'_'), ctClass);
			nameField.setModifiers(Modifier.PUBLIC);
			this.addField(ctClass, nameField);
		}

		ClassFile classFile = ctClass.getClassFile();

		AnnotationsAttribute attr = new AnnotationsAttribute(
				classFile.getConstPool(), AnnotationsAttribute.visibleTag);
		
		Annotation annotation;
		if ( this.isEmbeddable()) {
			annotation = new Annotation(Embeddable.class.getName(),
					classFile.getConstPool());
		} else {
			annotation = new Annotation(Entity.class.getName(),
					classFile.getConstPool());
		}
		attr.addAnnotation(annotation);
		classFile.addAttribute(attr);
		if (this.isIdFieldNeeded()) {
			this.addIdField(ctClass);
		}

		return (Class<Object>) ctClass.toClass();

	}

	public boolean isIdFieldNeeded() {
		return idFieldNeeded;
	}

	public void setIdFieldNeeded(boolean idFieldNeeded) {
		this.idFieldNeeded = idFieldNeeded;
	}

	public boolean isEmbeddable() {
		return isEmbeddable;
	}

	public void setEmbeddable(boolean isEmbeddable) {
		this.isEmbeddable = isEmbeddable;
	}
	public String getSuperClassName() {
		return superClassName;
	}
}
