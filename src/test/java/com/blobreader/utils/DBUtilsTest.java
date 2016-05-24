package com.blobreader.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BlobResourceUtils.class, DBUtils.class, DriverManager.class, DBFactory.class })
@PowerMockIgnore("javax.management.*")
public class DBUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void blobRetrievedIsTheBlobReturned() throws SQLException, Exception {

		// Given
		byte[] data = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Blob blobMock = mock(Blob.class);
		when(blobMock.length()).thenReturn(1L);
		when(blobMock.getBytes(anyLong(), anyInt())).thenReturn(data);

		DBUtils spy = PowerMockito.spy(new DBUtils());

		PowerMockito.doReturn(blobMock)
				.when(spy, PowerMockito.method(DBUtils.class, "retrieveBlobFromDatabase", Connection.class))
				.withArguments(Mockito.anyObject());

		PowerMockito.mockStatic(DBFactory.class);
		PowerMockito.when(DBFactory.getConnection()).thenReturn(mock(Connection.class));

		// When
		byte[] blobBytes = spy.blobDBExtract();

		// Then
		PowerMockito.verifyStatic();
		assertEquals(data, blobBytes);

	}

}
