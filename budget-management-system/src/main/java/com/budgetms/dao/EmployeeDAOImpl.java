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
        String sql = "SELECT id, name, role, salary, active, department_id FROM employee WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find employee with id: " + id, e);
        }
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT id, name, role, salary, active, department_id FROM employee";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load employees", e);
        }

        return employees;
    }

    @Override
    public List<Employee> findByDepartmentId(int departmentId) {
        String sql = "SELECT id, name, role, salary, active, department_id FROM employee WHERE department_id = ?";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load employees for department id: " + departmentId, e);
        }

        return employees;
    }

    // Turns one row of the ResultSet into an Employee object.

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("role"),
                rs.getDouble("salary"),
                rs.getInt("department_id"),
                rs.getBoolean("active")
        );
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employee SET name = ?, role = ?, salary = ?, active = ?, department_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getRole());
            stmt.setDouble(3, employee.getSalary());
            stmt.setBoolean(4, employee.isActive());
            stmt.setInt(5, employee.getDepartmentId());
            stmt.setInt(6, employee.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee with id: " + employee.getId(), e);
        }
    }

    @Override
    public void deactivate(int id) {
        // Employees are never deleted, only marked inactive to  preserve  historical salary and headcount data

        String sql = "UPDATE employee SET active = false WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to deactivate employee with id: " + id, e);
        }
    }
}