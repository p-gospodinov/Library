package com.academy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class
JDBCConnectionManager {
    private static JDBCConnectionManager instance;
    private Connection connection;

    private JDBCConnectionManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized JDBCConnectionManager getInstance() {
        if (instance == null) {
            instance = new JDBCConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/academy", "dbuser", "1234");
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

