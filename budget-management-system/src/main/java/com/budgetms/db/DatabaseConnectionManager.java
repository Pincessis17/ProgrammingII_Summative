package com.budgetms.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Opens a JDBC connection to the MySQL database used by the application.
// Reads connection details from db.properties instead of hardcoding them,
// so credentials never appear in source code or version control.
public class DatabaseConnectionManager {

    private static final Properties props = new Properties();

    // Loads db.properties once when this class is first used.
    static {
        try (InputStream input = DatabaseConnectionManager.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException(
                        "db.properties not found. Copy db.properties.example, rename it, and fill in your own credentials."
                );
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
        );
    }
}