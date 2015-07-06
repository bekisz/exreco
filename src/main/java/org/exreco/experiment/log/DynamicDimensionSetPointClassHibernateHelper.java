package org.exreco.experiment.log;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.MemberValue;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.dim.DimensionValue;
import org.exreco.experiment.persistence.DynamicClass;

public class DynamicDimensionSetPointClassHibernateHelper extends
		DynamicClassHibernateHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7238495428732159917L;

	/**
	 * 
	 */

	public DynamicDimensionSetPointClassHibernateHelper(String lineageClassName) {
		super(lineageClassName);
		this.setIdFieldNeeded(true);
	}

	public DynamicDimensionSetPointClassHibernateHelper() {
		this("org.exreco.experiment.persistence.dao.DynamicDimensionSetPoint");

	}

	synchronized public DynamicClass createDynamicObject(
			DimensionSetPoint dimSetPoint) throws Exception {
		return this.createDynamicObject(dimSetPoint,
				Collections.<String, Object> emptyMap());
	}

	synchronized public DynamicClass createDynamicObject(
			DimensionSetPoint dimSetPoint,
			Map<String, Object> additionalAttributes) throws Exception {
		Map<String, Object> attributesMap = new HashMap<String, Object>(
				additionalAttributes);

		for (DimensionValue dimValue : dimSetPoint) {
			attributesMap.put(dimValue.getDimensionType().getName(),
					dimValue.getValue());
		}
		DynamicClass object = (DynamicClass) this
				.createDynamicObject(attributesMap);
		return object;
	}

	synchronized public void persist(DimensionSetPoint dimSetPoint)
			throws Exception {
		Map<String, Object> attributesMap = new HashMap<String, Object>();

		for (DimensionValue dimValue : dimSetPoint) {
			attributesMap.put(dimValue.getDimensionType().getName(),
					dimValue.getValue());
		}

		super.persist(attributesMap);
	}

	protected void addField(CtClass ctClass, CtField nameField)
			throws CannotCompileException {
		/*
		if ("caseId".equalsIgnoreCase(nameField.getName())) {
			ClassPool cp = ClassPool.getDefault();
			ClassFile classFile = ctClass.getClassFile();

			AnnotationsAttribute attribute = new AnnotationsAttribute(
					classFile.getConstPool(), AnnotationsAttribute.visibleTag);

			Annotation columnAnnotation = new Annotation(
					Column.class.getName(), classFile.getConstPool());
			columnAnnotation.addMemberValue("unique", new BooleanMemberValue(true, new ConstPool(null)));
			attribute.addAnnotation(columnAnnotation);
			
			nameField.getFieldInfo().addAttribute(attribute);
		} */
		super.addField(ctClass, nameField);

	}

}
