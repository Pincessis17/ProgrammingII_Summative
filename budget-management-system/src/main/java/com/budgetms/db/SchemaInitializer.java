package com.budgetms.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    public static void initializeSchema() {
        String createDepartment = """
            CREATE TABLE IF NOT EXISTS department (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                parent_department_id INT NULL,
                FOREIGN KEY (parent_department_id) REFERENCES department(id)
            )
            """;

        String createEmployee = """
            CREATE TABLE IF NOT EXISTS employee (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                role VARCHAR(100),
                salary DECIMAL(10,2) NOT NULL,
                active BOOLEAN NOT NULL DEFAULT TRUE,
                department_id INT NOT NULL,
                FOREIGN KEY (department_id) REFERENCES department(id)
            )
            """;

        String createBudget = """
            CREATE TABLE IF NOT EXISTS budget (
                id INT PRIMARY KEY AUTO_INCREMENT,
                department_id INT NOT NULL,
                period VARCHAR(20) NOT NULL,
                allocated_amount DECIMAL(12,2) NOT NULL,
                FOREIGN KEY (department_id) REFERENCES department(id)
            )
            """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Department must be created first... since both employee and budget refernce it.

            stmt.execute(createDepartment);
            stmt.execute(createEmployee);
            stmt.execute(createBudget);

            System.out.println("Schema initialized successfully.");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }
}