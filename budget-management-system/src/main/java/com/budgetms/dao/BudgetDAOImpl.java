package com.budgetms.dao;

import com.budgetms.db.DatabaseConnectionManager;
import com.budgetms.model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BudgetDAOImpl implements BudgetDAO {

    @Override
    public Budget create(Budget budget) {
        String sql = "INSERT INTO budget (department_id, period, allocated_amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, budget.getDepartmentId());
            stmt.setString(2, budget.getPeriod());
            stmt.setDouble(3, budget.getAllocatedAmount());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    budget.setId(keys.getInt(1));
                }
            }

            return budget;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create budget for department id: " + budget.getDepartmentId(), e);
        }
    }

    @Override
    public Optional<Budget> findById(int id) {
        String sql = "SELECT id, department_id, period, allocated_amount FROM budget WHERE id = ?";

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
            throw new RuntimeException("Failed to find budget with id: " + id, e);
        }
    }

    @Override
    public List<Budget> findAll() {
        String sql = "SELECT id, department_id, period, allocated_amount FROM budget";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                budgets.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load budgets", e);
        }

        return budgets;
    }

    @Override
    public Optional<Budget> findByDepartmentId(int departmentId) {
        String sql = "SELECT id, department_id, period, allocated_amount FROM budget WHERE department_id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find budget for department id: " + departmentId, e);
        }
    }

    @Override
    public void update(Budget budget) {
        String sql = "UPDATE budget SET department_id = ?, period = ?, allocated_amount = ? WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, budget.getDepartmentId());
            stmt.setString(2, budget.getPeriod());
            stmt.setDouble(3, budget.getAllocatedAmount());
            stmt.setInt(4, budget.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update budget with id: " + budget.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        // Unlike Department, a Budget has no dependents of its own — deleting an allocation record doesn't orphan any employees or departments, so no business-rule check is needed here, per the proposal.
        String sql = "DELETE FROM budget WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete budget with id: " + id, e);
        }
    }

    private Budget mapRow(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getInt("id"),
                rs.getInt("department_id"),
                rs.getString("period"),
                rs.getDouble("allocated_amount")
        );
    }
}