package utils;

import java.sql.Connection;

public class JdbcConnection {

	public static Connection connection;

	public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connection) {
		JdbcConnection.connection = connection;
	}
	
}
