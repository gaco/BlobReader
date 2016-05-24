package com.blobreader.utils;

import static com.blobreader.utils.BlobConstants.CONFIG_PATH;
import static com.blobreader.utils.BlobConstants.DB_PROPERTIES_FILENAME;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBFactory {

	private static DBFactory connection;
	private static final Logger logger = Logger.getLogger(DBFactory.class.getName());
	private String host;
	private String port;
	private String sid;
	private String user;
	private String pass;
	private String url;

	private DBFactory() {

	}

	private Connection createConnection() throws SQLException {

		String dbConnectionProperties = CONFIG_PATH.getValue() + DB_PROPERTIES_FILENAME.getValue();
		logger.debug("dbProperty: " + dbConnectionProperties);
		try {
			readConnectionProperties(dbConnectionProperties);
		} catch (ClassNotFoundException e) {
			logger.error("Oracle Driver is missing or incorrect.", e);
		} catch (IOException e) {
			logger.error("Failed to read database properties file (" + DB_PROPERTIES_FILENAME.getValue() + ").", e);
		}

		return DriverManager.getConnection(this.url, this.user, this.pass);
	}

	private void readConnectionProperties(String dbConnectionProperties) throws IOException, ClassNotFoundException {
		Properties properties = new Properties();

		FileInputStream fis = new FileInputStream(dbConnectionProperties);
		properties.load(fis);
		Class.forName(properties.getProperty("DB_DRIVER"));
		this.host = properties.getProperty("DB_HOST");
		this.port = properties.getProperty("DB_PORT");
		this.sid = properties.getProperty("DB_SID");
		this.user = properties.getProperty("DB_USERNAME");
		this.pass = properties.getProperty("DB_PASSWORD");

		url = "jdbc:oracle:thin:@" + this.host + ":" + this.port + ":" + this.sid;
	}

	public static Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = new DBFactory();
		}
		return connection.createConnection();
	}
}
