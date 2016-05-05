package com.blobreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import com.blobreader.utils.BlobConstants;
import com.blobreader.utils.BlobResourceUtils;
import com.blobreader.utils.DBUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;

public class BlobReader {

	private static final String OUTPUT_PATH = BlobConstants.OUTPUT_PATH.getValue();

	public static void main(String[] args) {
		init();

		Path path = null;
		DBUtils dbUtils = new DBUtils();
		Object entity = null;

		try {
			byte[] blobBytes = dbUtils.blobDBExtract(OUTPUT_PATH);
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
			System.err.println("ClassNotFoundException - Failed to find the class: " + e.getMessage());
			System.err.println("Missing jar in resouces/jars perhaps?");
		} catch (IOException e) {
			e.printStackTrace();
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
			System.out.format("XML file has been created: %s%n", path.toAbsolutePath().normalize().toString() + ".xml");
		} catch (Exception e) {
			System.err.println("Failed to save the Blob as XML.");
		}

	}

	private static void toJSON(Path path, Object entity) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writerWithDefaultPrettyPrinter()
					.writeValue(new FileOutputStream(OUTPUT_PATH + path.getFileName().toString() + ".json"), entity);
			System.out.format("JSON file has been created: %s%n",
					path.toAbsolutePath().normalize().toString() + ".json");
		} catch (Exception e) {
			System.err.println("Failed to save the Blob as JSON.");
		}
	}

}
