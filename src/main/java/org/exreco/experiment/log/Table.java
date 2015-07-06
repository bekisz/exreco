package org.exreco.experiment.log;

public interface Table extends Insertable {
	public String getName();

	public void init(Session session, String tableName) throws Exception;

	public void close() throws Exception;

	void init(Session session, String tableName, boolean rewrite)
			throws Exception;

}
