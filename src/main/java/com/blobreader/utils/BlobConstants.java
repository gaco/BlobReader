package com.blobreader.utils;

public enum BlobConstants {
//	RESOURCES_DIR ("src/main/resources/"),
	EAR_JARS_LOCATION ("main/resources/jars/"),
	BLOB_TARGET_FOLDER_NAME ("output/"),
	OUTPUT_PATH (System.getProperty("user.dir") + "/" + BLOB_TARGET_FOLDER_NAME.getValue()),
//	CONFIG_PATH (RESOURCES_DIR.getValue() + "/" + "config/"),
	CONFIG_PATH ( "/" + "config/"),
	DB_PROPERTIES_FILENAME ("db.properties"),
	SQL_STATEMENT_PROPERTIES_FILENAME ("sql_statement.properties");
	
	private String value;

	private BlobConstants(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
