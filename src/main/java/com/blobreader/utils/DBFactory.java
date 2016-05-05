package com.blobreader.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBFactory {
	public static Connection getConnection(String url, String user, String pass) throws ClassNotFoundException {
		try {
			return DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
