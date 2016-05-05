package com.blobreader.utils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DBFactory.class)
@PowerMockIgnore("javax.management.*")
public class DBFactoryTest {

	@Test
	public final void test() throws SQLException {
		// given
		PowerMockito.mockStatic(DriverManager.class);
		PowerMockito.when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mock(Connection.class));
		
		// when
		DriverManager.getConnection(anyString(), anyString(), anyString());

		// then
		PowerMockito.verifyStatic();
	}

}
