package Server;

import java.sql.*;

public class Database {
	private final String DB_NAME = "ioki";
	private final String DB_USER = "root";
	private final String DB_PASS = "";
	private final String DB_HOST = "localhost";
	private final int DB_PORT = 3306;
	private Connection connection = null;

	public boolean open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME, DB_USER, DB_PASS);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getSecret(String id) {
		String secret = null;
		ResultSet resultSet = null;
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT secret FROM locks WHERE id = ?");
			statement.setInt(1, Integer.parseInt(id));
			resultSet = statement.executeQuery();
			if (resultSet.next())
				secret = resultSet.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return secret;
	}

	public boolean canAccess(String user, String lock) {
		boolean canAccess = false;
		ResultSet resultSet = null;
		try {
			PreparedStatement statement = connection.prepareStatement(
					"SELECT id FROM locks WHERE user = ? AND id = ? UNION " +
					"SELECT id FROM shared_locks WHERE shared_to = ? AND lock_id = ? AND approved = 1");
			statement.setString(1, user);
			statement.setString(2, lock);
			statement.setString(3, user);
			statement.setString(4, lock);
			resultSet = statement.executeQuery();
			canAccess = resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return canAccess;
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}