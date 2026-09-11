package com.budgetms.dao;

import com.budgetms.db.DatabaseConnectionManager;
import com.budgetms.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public Employee create(Employee employee) {
        String sql = "INSERT INTO employee (name, role, salary, active, department_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getRole());
            stmt.setDouble(3, employee.getSalary());
            stmt.setBoolean(4, employee.isActive());
            stmt.setInt(5, employee.getDepartmentId());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    employee.setId(keys.getInt(1));
                }
            }

            return employee;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee: " + employee.getName(), e);
        }
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() {
        return List.of();
    }

    @Override
    public List<Employee> findByDepartmentId(int departmentId) {
        return List.of();
    }

    @Override
    public void update(Employee employee) {

    }

    @Override
    public void deactivate(int id) {

    }
}