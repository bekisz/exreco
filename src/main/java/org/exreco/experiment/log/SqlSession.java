package org.exreco.experiment.log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SqlSession extends Session implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -797919558371647812L;
	private static Logger logger = LogManager.getLogger(SqlSession.class
			.getName());

	static private Connection connection = null;

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		if (connection == null) {
			this.init();
		}
		return connection;
	}




	synchronized public void init() {
		try {
			if (SqlSession.connection == null) {
				Class.forName(this.getDriver()).newInstance();
				SqlSession.connection = DriverManager.getConnection(
						this.getUrl(), this.getUser(), this.getPassword());
		

				logger.debug("Database connection established with class : "
						+ this.getClass().getName() + " connection "
						+ SqlSession.connection);
			}
		} catch (Exception e) {
			logger.error("Cannot connect to database server", e);
		} finally {
			// this.close();
		}
	}

	@Override
	public Table createTable(String tableName, boolean rewrite)
			throws Exception {
		SqlTable table = new SqlTable();

		table.init(this, tableName, rewrite);
		return table;

	}



	@Override
	public void close() {
		if (this.getConnection() != null) {
			try {
				this.getConnection().close();
				logger.debug("Database connection closed");

			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();

		this.init();


	}

	@Override
	protected void finalize() {
		//this.close();

	}
	public String toString() {
			StringBuffer sb = new StringBuffer(80);
			sb.append("Driver class : ").append(this.getDriver());
			sb.append("\nUrl : ").append(this.getUrl());
			sb.append("\nUser : ").append(this.getUser());
			
			return sb.toString();
	}
}
