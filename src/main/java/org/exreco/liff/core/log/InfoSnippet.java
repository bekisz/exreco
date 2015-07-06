package org.exreco.liff.core.log;

import java.io.Serializable;

public interface InfoSnippet extends Serializable {
	public String getName();

	public void reset();

	public Object renderValue();

	public Class<?> getType();

}