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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.budgetms.dao.DepartmentNotEmptyException;

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
        String sql = "SELECT id, name, parent_department_id FROM department";

        Map<Integer, Department> departmentsById = new HashMap<>();
        Map<Integer, Integer> parentIdByDepartmentId = new HashMap<>();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int parentId = rs.getInt("parent_department_id");
                boolean hasParent = !rs.wasNull();

                Department department = new Department(id, name, null);
                departmentsById.put(id, department);

                if (hasParent) {
                    parentIdByDepartmentId.put(id, parentId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load departments", e);
        }

        for (Map.Entry<Integer, Integer> entry : parentIdByDepartmentId.entrySet()) {
            Department child = departmentsById.get(entry.getKey());
            Department parent = departmentsById.get(entry.getValue());
            if (parent != null) {
                parent.addChild(child);
            }
        }

        return new ArrayList<>(departmentsById.values());
    }



    @Override
    public void update(Department department) {
        String sql = "UPDATE department SET name = ?, parent_department_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department.getName());

            if (department.getParent() != null) {
                stmt.setInt(2, department.getParent().getId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setInt(3, department.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update department with id: " + department.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        //  a department cannot bedeleted while it still has employees or child departments.

        String checkEmployees = "SELECT COUNT(*) FROM employee WHERE department_id = ?";
        String checkChildren = "SELECT COUNT(*) FROM department WHERE parent_department_id = ?";
        String deleteSql = "DELETE FROM department WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(checkEmployees)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new DepartmentNotEmptyException(
                                "Cannot delete department: it still has employees assigned to it."
                        );
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(checkChildren)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new DepartmentNotEmptyException(
                                "Cannot delete department: it still has employees assigned to it."
                        );
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete department with id: " + id, e);
        }
    }
}