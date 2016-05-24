package com.blobreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import com.blobreader.utils.BlobConstants;
import com.blobreader.utils.BlobResourceUtils;
import com.blobreader.utils.DBUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;

public class BlobReader {

	private static final String OUTPUT_PATH = BlobConstants.OUTPUT_PATH.getValue();
	private static final Logger logger = Logger.getLogger(BlobReader.class.getName());

	private BlobReader() {

	}

	public static void main(String[] args) {
		init();

		Path path = null;
		DBUtils dbUtils = new DBUtils();
		Object entity = null;

		try {
			byte[] blobBytes = dbUtils.blobDBExtract();
			if (blobBytes != null) {
				path = BlobResourceUtils.saveBlobFileToFilesystem(blobBytes, OUTPUT_PATH);
			}
			if (path != null) {
				entity = BlobResourceUtils.retrieveBlobFromFilesystem(path.getFileName().toString(), OUTPUT_PATH);
			}
			if (entity != null) {
				toJSON(path, entity);

				toXML(path, entity);
			}
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException - Failed to find the class: " + e);
			logger.error("Missing jar file in jars/ directory perhaps?\n" + e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private static void init() {
		File file = new File(OUTPUT_PATH);
		file.mkdir();
	}

	private static void toXML(Path path, Object entity) {
		XStream xs = new XStream();
		try {
			xs.toXML(entity, new FileOutputStream(OUTPUT_PATH + path.getFileName().toString() + ".xml"));
			logger.info("XML file has been created: " + path.toAbsolutePath().normalize().toString() + ".xml");
		} catch (Exception e) {
			logger.error("Failed to save the Blob as XML.", e);
		}

	}

	private static void toJSON(Path path, Object entity) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writerWithDefaultPrettyPrinter()
					.writeValue(new FileOutputStream(OUTPUT_PATH + path.getFileName().toString() + ".json"), entity);
			logger.info("JSON file has been created: %s%n" + path.toAbsolutePath().normalize().toString() + ".json");
		} catch (Exception e) {
			logger.error("Failed to save the Blob as JSON.", e);
		}
	}

}
