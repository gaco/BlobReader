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

/**
 * Class responsible to connect to the database and extract the blob file from it.
 * For this, two properties file must exists. One that should contains the database connection and 
 * one that should contain the query to be performed to find the desired blob.
 * @author gabriel
 */
public class DBUtils {

	private String blobColumn;
	private String fromWhereClause;

	/**
	 * Retrieves the Blob from the configured database (database details as well as the query used to extract the blob
	 * should be defined in a property file) and
	 * extracts it to the input chosen directory. 
	 * @param blobPathToExtract path where the extracted blob should be saved in the filesystem 
	 * @return byte array representing the extracted blob.
	 */
	public byte[] blobDBExtract(String blobPathToExtract) {
		byte[] blobBytes = null;
		Blob blob = null;
		
		Connection connection = initDBConnection();


		if (connection != null) {
			try {
				blob = retrieveBlobFromDatabase(connection);
				if (blob == null) {
					System.out.println("SQL Statement ended with no results");
				} else {
					blobBytes = blob.getBytes(1, (int) blob.length());
					blob.free();
				}
			} catch (SQLException e) {
				if (blob != null) {
					System.err.println("Failed to release the Blob from memory.");
				} else {
					System.err.println("Failed to execute the Statement. Please, check your query is right.");
				}
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					System.err.println("Failed to close the Database connection.");
				}
			}
		}
		return blobBytes;
	}

	private Blob retrieveBlobFromDatabase(Connection connection) throws SQLException {
		try {
			PreparedStatement statement = queryBuilder(connection);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				Blob blob = result.getBlob(blobColumn);
				return blob;
			}
		} catch (SQLException e) {
			throw e;

		}
		return null;
	}

	private PreparedStatement queryBuilder(Connection connection) throws SQLException {
		blobColumn = null;
		fromWhereClause = null;
		try {

			FileInputStream fis = null;
			Properties properties = new Properties();
			String sqlProperties = getClass().getResource(CONFIG_PATH.getValue() + SQL_STATEMENT_PROPERTIES_FILENAME.getValue()).getPath();
			System.out.println(sqlProperties);
			fis = new FileInputStream(sqlProperties);
			properties.load(fis);

			blobColumn = properties.getProperty("BLOB_COLUMN");
			fromWhereClause = properties.getProperty("FROM_WHERE_CLAUSE");

		} catch (IOException e) {
			System.err.println(
					"Failed to read the query properties file (" + SQL_STATEMENT_PROPERTIES_FILENAME.getValue() + ").");
		}
		String selectStatement = "SELECT " + blobColumn + " " + fromWhereClause;
		PreparedStatement statement = connection.prepareStatement(selectStatement);
		return statement;
	}

	private Connection initDBConnection() {
		Connection connection = null;
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {

			String dbConnectionProperties = getClass().getResource(CONFIG_PATH.getValue() + DB_PROPERTIES_FILENAME.getValue()).getPath();
			System.out.println(dbConnectionProperties);
			fis = new FileInputStream(dbConnectionProperties);
			properties.load(fis);

			Class.forName(properties.getProperty("DB_DRIVER"));
			String host = properties.getProperty("DB_HOST");
			String port = properties.getProperty("DB_PORT");
			String sid = properties.getProperty("DB_SID");
			String user = properties.getProperty("DB_USERNAME");
			String pass = properties.getProperty("DB_PASSWORD");
			String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;

			System.out.println("Testing Oracle Connection...");
			connection = DBFactory.getConnection(url, user, pass);
			if (connection != null) {
				System.out.println("Oracle JDBC Connected.");
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Oracle Driver is missing or incorrect.");
		} catch (IOException e) {
			System.err.println("Failed to read database properties file (" + DB_PROPERTIES_FILENAME.getValue() + ").");
		} catch (Exception e) {
			System.err.println("Failed to initiate the Database connection.");
		}
		return connection;
	}

}
