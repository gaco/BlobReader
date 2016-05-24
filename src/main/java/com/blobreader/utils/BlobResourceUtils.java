package com.blobreader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

/**
 * Utility class, used to perform operation in the filesystem related to a blob
 * file. Operations such as saving the blob and reading it from the system.
 * 
 * @author gabriel
 */
public class BlobResourceUtils {

	private static Logger logger = Logger.getLogger(BlobResourceUtils.class.getName());

	private BlobResourceUtils() {

	}

	/**
	 * Retrieves the blob from the system. It receives the blob file as
	 * parameter as well as its location. Once it is retrieved, it attempts to
	 * deserializes it. In case
	 * 
	 * @param blobName
	 *            the name of the blob file to be retrieved.
	 * @param filePath
	 *            path where blob file was previously saved in the file system.
	 * @return blob file as Object
	 * @throws ClassNotFoundException
	 *             - Class of a serialized object cannot be found
	 * @throws IOException
	 *             - if occurred some failure to read the blob from the file
	 *             system or to deserialize the blob object.
	 */
	public static Object retrieveBlobFromFilesystem(String blobName, String filePath) throws ClassNotFoundException {
		ObjectInputStream ois = null;
		try (FileInputStream fileInputStream = new FileInputStream(filePath + File.separator + blobName)) {
			ois = new ObjectInputStream(fileInputStream);
			return ois.readObject();
		} catch (FileNotFoundException e) {
			logger.error("Failed to read the blob from path: " + filePath);
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return ois;
	}

	/**
	 * Saves the blob, temporarily,  in the file system. It receives an array of bytes,
	 * representing the blob file deserialized. It also receives the location
	 * where it should be saved.
	 * 
	 * @param blobBytes
	 *            represents the blob object.
	 * @param filePath
	 *            location in the file system where the blob will be saved.
	 * @return full filepath where the blob was saved.
	 * @throws IOException
	 *             - If failed to save the blob in filesystem.
	 */
	public static Path saveBlobFileToFilesystem(byte[] blobBytes, String filePath) throws IOException {
		Path file = null;
		try {
			File resourcesFolder = new File(filePath);
			if (blobBytes.length > 0) {
				file = Files.createTempFile(resourcesFolder.toPath(), "entity_", ".blob");
				logger.info("Temporary blob file has been created: " + file);
				Files.write(file, blobBytes);
				file.toFile().deleteOnExit();
			} else {
				logger.warn("Blob is Empty.");
			}
		} catch (IOException e) {
			throw new IOException("Failed to save in the extracted Blob in filesystem.", e);
		}
		return file;
	}
}
