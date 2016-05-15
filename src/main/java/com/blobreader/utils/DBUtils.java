package com.blobreader.utils;

import static com.blobreader.utils.BlobConstants.CONFIG_PATH;
import static com.blobreader.utils.BlobConstants.DB_PROPERTIES_FILENAME;
import static com.blobreader.utils.BlobConstants.SQL_STATEMENT_PROPERTIES_FILENAME;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Class responsible to connect to the database and extract the blob file from
 * it. For this, two properties file must exists. One that should contains the
 * database connection and one that should contain the query to be performed to
 * find the desired blob.
 * 
 * @author gabriel
 */
public class DBUtils {

	private String blobColumn;
	private String fromWhereClause;
	private static Logger logger = Logger.getLogger(DBUtils.class.getName());

	/**
	 * Retrieves the Blob from the configured database (database details as well
	 * as the query used to extract the blob should be defined in a property
	 * file) and extracts it to the input chosen directory.
	 * 
	 * @param blobPathToExtract
	 *            path where the extracted blob should be saved in the
	 *            filesystem
	 * @return byte array representing the extracted blob.
	 */
	public byte[] blobDBExtract() {
		byte[] blobBytes = null;
		Blob blob = null;

		Connection connection = initDBConnection();

		if (connection != null) {
			try {
				blob = retrieveBlobFromDatabase(connection);
				if (blob == null) {
					logger.info("SQL Statement ended with no results");
				} else {
					blobBytes = blob.getBytes(1, (int) blob.length());
					blob.free();
				}
			} catch (SQLException e) {
				if (blob != null) {
					logger.error("Failed to release the Blob from memory.", e);
				} else {
					logger.error("Failed to execute the Statement. Please, make sure your query is right.", e);
				}
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("Failed to close the Database connection.", e);
				}
			}
		}
		return blobBytes;
	}

	private Blob retrieveBlobFromDatabase(Connection connection) throws SQLException {
		PreparedStatement statement = queryBuilder(connection);
		ResultSet result = statement.executeQuery();
		if (result.next()) {
			return result.getBlob(blobColumn);
		}
		return null;
	}

	private PreparedStatement queryBuilder(Connection connection) throws SQLException {
		blobColumn = null;
		fromWhereClause = null;
		try {

			FileInputStream fis;
			Properties properties = new Properties();
			String sqlProperties = getClass()
					.getResource(CONFIG_PATH.getValue() + SQL_STATEMENT_PROPERTIES_FILENAME.getValue()).getPath();
			logger.info(sqlProperties);
			fis = new FileInputStream(sqlProperties);
			properties.load(fis);

			blobColumn = properties.getProperty("BLOB_COLUMN");
			fromWhereClause = properties.getProperty("FROM_WHERE_CLAUSE");

		} catch (IOException e) {
			logger.error(
					"Failed to read the query properties file (" + SQL_STATEMENT_PROPERTIES_FILENAME.getValue() + ").",
					e);
		}
		String selectStatement = "SELECT " + blobColumn + " " + fromWhereClause;
		return connection.prepareStatement(selectStatement);
	}

	private Connection initDBConnection() {
		Connection connection = null;
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {

			String dbConnectionProperties = getClass()
					.getResource(CONFIG_PATH.getValue() + DB_PROPERTIES_FILENAME.getValue()).getPath();
			logger.info(dbConnectionProperties);
			fis = new FileInputStream(dbConnectionProperties);
			properties.load(fis);

			Class.forName(properties.getProperty("DB_DRIVER"));
			String host = properties.getProperty("DB_HOST");
			String port = properties.getProperty("DB_PORT");
			String sid = properties.getProperty("DB_SID");
			String user = properties.getProperty("DB_USERNAME");
			String pass = properties.getProperty("DB_PASSWORD");
			String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;

			logger.info("Testing Oracle Connection...");
			connection = DBFactory.getConnection(url, user, pass);
			logger.info("Oracle JDBC Connected.");
		} catch (ClassNotFoundException e) {
			logger.error("Oracle Driver is missing or incorrect.", e);
		} catch (IOException e) {
			logger.error("Failed to read database properties file (" + DB_PROPERTIES_FILENAME.getValue() + ").", e);
		} catch (Exception e) {
			logger.error("Failed to initiate the Database connection.", e);
		}
		return connection;
	}

}
