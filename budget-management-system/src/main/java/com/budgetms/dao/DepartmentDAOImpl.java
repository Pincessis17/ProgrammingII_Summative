package com.budgetms.dao;

import com.budgetms.db.DatabaseConnectionManager;
import com.budgetms.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public Department create(Department department) {
        String sql = "INSERT INTO department (name, parent_department_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, department.getName());

            // A top-level department has no parent, so we store NULL in that case.
            if (department.getParent() != null) {
                stmt.setInt(2, department.getParent().getId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            // MySQL generated the id automatically ... so the Java object matches what's actually stored in the database.
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    department.setId(keys.getInt(1));
                }
            }

            return department;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create department: " + department.getName(), e);
        }
    }

    @Override
    public Optional<Department> findById(int id) {
        String sql = "SELECT id, name, parent_department_id FROM department WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department department = new Department(
                            rs.getInt("id"),
                            rs.getString("name"),
                            null // parent resolved separately — see note below
                    );
                    return Optional.of(department);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find department with id: " + id, e);
        }
    }

    @Override
    public List<Department> findAll() {
        return List.of();
    }

    @Override
    public void update(Department department) {

    }

    @Override
    public void delete(int id) {

    }
}