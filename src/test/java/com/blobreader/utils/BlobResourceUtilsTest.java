package com.blobreader.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.logging.impl.Log4JCategoryLog;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.velocity.runtime.log.Log4JLogSystem;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.generator.HamcrestFactoryWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import junit.framework.Assert;
import oracle.sql.BLOB;

/**
 * Estrutura de um Teste Unitário:
 * - Given -> Pré-condições 
 * - When -> Behavior/Comportamento
 * - Then -> Pós-condições
 * Bom teste: Testa comportamento, não a implementação
 * STUB: Provê uma resposta pronta para o retorno de um método
 * MOCK: Provê uma resposta pronta para o retorno de um método E verifica que ele é realmente chamado.
 */
public class BlobResourceUtilsTest {

	private static Logger logger = Logger.getLogger(BlobResourceUtils.class.getName());

	private static final String BLOB_FILE_EXTENTION = ".blob";
	
	// Name of the temporary blob file
	private static String FILE_NAME;
	
	// Path of the temporary blob file
	private static String FILE_PATH;
	
	// Temporary File Full Pathname (Path + Name)
	private static String FULL_FILE_PATHNAME;
	
	private File file;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		URL resource = this.getClass().getResource("");
		Path path= Paths.get(resource.toURI()).normalize();
		file = Files.createTempFile(path, "blob_", ".tmp").normalize().toFile();
		FILE_NAME = file.getName();
		FILE_PATH = path.toString();
		FULL_FILE_PATHNAME = FILE_PATH + File.separator + FILE_NAME;
		file.deleteOnExit();
	}

	@After
	public void tearDown() {
		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void shouldRetrieveBlobObjectFromFilesystem() throws IOException, SQLException, ClassNotFoundException {
		// Given
		readsTheTemporaryFileAndWriteAnEmptyBlobObjectOnIt();

		// When
		Object actualObject = BlobResourceUtils.retrieveBlobFromFilesystem(FILE_NAME, FILE_PATH);

		// Then
		assertTrue("A Blob object should be retrieved", actualObject instanceof Blob);
	}

	private void readsTheTemporaryFileAndWriteAnEmptyBlobObjectOnIt() throws IOException, FileNotFoundException, SQLException {
		ObjectOutputStream oos = null;
		oos = new ObjectOutputStream(new FileOutputStream(FULL_FILE_PATHNAME));
		BLOB emptyBLOB = BLOB.getEmptyBLOB();
		oos.writeObject(emptyBLOB);
		oos.close();
	}

	@Test
	public void blobFileNotFoundFromFileSYstem() throws IOException, SQLException, ClassNotFoundException {
		// given
		StringWriter stringWriter = new StringWriter();
		WriterAppender writerAppender = new WriterAppender(new PatternLayout(), stringWriter);
		logger.addAppender(writerAppender);
		
		//when 
		BlobResourceUtils.retrieveBlobFromFilesystem("name_that_doesnt_exist", FILE_PATH.toString());
		
		assertTrue(stringWriter.toString().contains("Failed to read the blob from path"));
		
	}
	
//	@Test(expected = IOException.class)
//	public void errorToReadTheBlobContent() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
//		BlobResourceUtils.retrieveBlobFromFilesystem(FILE_NAME, FILE_PATH.toString());
//		
//	}
	
	@Test
	public void shouldSaveBlobFileToFilesystem() throws SQLException, IOException {
		// Given
		
		byte[] blobBytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		// When
		Path actualPath = BlobResourceUtils.saveBlobFileToFilesystem(blobBytes, FILE_PATH.toString());
		
		
		// Then
		MatcherAssert.assertThat("A Blob file must be saved in the file system", actualPath.toFile().toString(), Matchers.endsWith(BLOB_FILE_EXTENTION));
	}

	@Test
	public void shouldNotSaveBlobFileToFilesystemBecauseBlobIsEmpty() throws SQLException, IOException {
		
		byte[] blobBytes = {};
		
		Path actualPath = BlobResourceUtils.saveBlobFileToFilesystem(blobBytes, FILE_PATH.toString());
		
		assertEquals(null, actualPath);
	}
	
	
	@Test
	public void triedSaveBlobToFileSystemButPathIsInvalid() throws SQLException, IOException {
		// Then
		thrown.expect(IOException.class);
		thrown.expectMessage("Failed to save in the extracted Blob in filesystem.");
		
		// Given
		byte[] blobBytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		// When
		BlobResourceUtils.saveBlobFileToFilesystem(blobBytes, "/path/does/not/exists");
	}

}
