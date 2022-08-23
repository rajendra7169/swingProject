package com.java.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtility {
    public static Connection getDbConnection() throws SQLException {
        String connectionURL = "jdbc:mysql://localhost:3306/Covid19DB?useSSL=false";

        return DriverManager.getConnection(connectionURL, "root", "root");
    }
}
