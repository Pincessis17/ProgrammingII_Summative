package com.budgetms.dao;

import com.budgetms.model.Budget;
import com.budgetms.model.Department;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BudgetDaoTest {

    private final BudgetDAO budgetDAO = new BudgetDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    private int testDepartmentId;
    private int createdBudgetId = -1;

    @BeforeEach
    void setUp() {
        // Every Budget needs a real department to belong to, so we create a fresh, disposable one for each test — same pattern as EmployeeDaoTest.
        Department department = departmentDAO.create(new Department(0, "Test Dept For Budgets", null));
        testDepartmentId = department.getId();
    }

    @AfterEach
    void cleanUp() {
        if (createdBudgetId != -1) {
            budgetDAO.delete(createdBudgetId);
            createdBudgetId = -1;
        }
        // Nothing references a budget, but a department can't be deleted while it has one assigned via foreign key — so budget must go first.
        departmentDAO.delete(testDepartmentId);
    }

    @Test
    void create_savesBudgetAndAssignsId() {
        Budget budget = new Budget(0, testDepartmentId, "2026-Q1", 10000.0);

        Budget saved = budgetDAO.create(budget);
        createdBudgetId = saved.getId();

        assertTrue(saved.getId() > 0);
    }

    @Test
    void findById_existingBudget_returnsIt() {
        Budget budget = budgetDAO.create(new Budget(0, testDepartmentId, "2026-Q1", 8000.0));
        createdBudgetId = budget.getId();

        Optional<Budget> result = budgetDAO.findById(createdBudgetId);

        assertTrue(result.isPresent());
        assertEquals(8000.0, result.get().getAllocatedAmount());
    }

    @Test
    void findByDepartmentId_returnsCorrectBudget() {
        Budget budget = budgetDAO.create(new Budget(0, testDepartmentId, "2026-Q1", 6000.0));
        createdBudgetId = budget.getId();

        Optional<Budget> result = budgetDAO.findByDepartmentId(testDepartmentId);

        assertTrue(result.isPresent());
        assertEquals(testDepartmentId, result.get().getDepartmentId());
    }

    @Test
    void update_changesAllocatedAmount() {
        Budget budget = budgetDAO.create(new Budget(0, testDepartmentId, "2026-Q1", 5000.0));
        createdBudgetId = budget.getId();

        budget.setAllocatedAmount(7500.0);
        budgetDAO.update(budget);

        Optional<Budget> result = budgetDAO.findById(createdBudgetId);
        assertEquals(7500.0, result.get().getAllocatedAmount());
    }

    @Test
    void delete_removesBudget() {
        Budget budget = budgetDAO.create(new Budget(0, testDepartmentId, "2026-Q1", 4000.0));
        int id = budget.getId();

        budgetDAO.delete(id);

        Optional<Budget> result = budgetDAO.findById(id);
        assertTrue(result.isEmpty());
        // createdBudgetId stays -1 — already deleted, cleanUp() has nothing to do
    }
}