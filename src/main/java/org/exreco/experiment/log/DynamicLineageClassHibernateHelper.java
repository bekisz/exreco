package org.exreco.experiment.log;

import java.io.Serializable;

public class DynamicLineageClassHibernateHelper extends
		DynamicClassHibernateHelper implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -742616684272300428L;
	
	public DynamicLineageClassHibernateHelper(String lineageClassName) {
		super(lineageClassName);
	}

	protected synchronized Object  convertMapValue2FieldValue(String key, Object value) {
		
		
		if (value instanceof Double) {
			Double doubleValue = (double) value;
			if (Double.isNaN(doubleValue)) {
				return null;
			}
		}
		
		return value;
	}
}
