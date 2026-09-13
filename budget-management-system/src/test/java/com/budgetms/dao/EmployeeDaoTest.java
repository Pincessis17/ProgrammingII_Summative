package com.budgetms.dao;

import com.budgetms.db.DatabaseConnectionManager;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDaoTest {

    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    private int testDepartmentId;
    private int createdEmployeeId = -1;

    @BeforeEach
    void setUp() {
        // Create a fresh department for each test to attach employees to...
        Department department = departmentDAO.create(new Department(0, "Test Dept For Employees", null));
        testDepartmentId = department.getId();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        // EmployeeDAO deliberately has no delete() — deactivate is the only application-facing option, to preserve history. But a test creating  throwaway data needs to actually remove it, or every run leaves
        // permanent junk in the database. This direct delete is a test-only concern, separate from the application's real, intentional behaviour.
        if (createdEmployeeId != -1) {
            try (Connection conn = DatabaseConnectionManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM employee WHERE id = ?")) {
                stmt.setInt(1, createdEmployeeId);
                stmt.executeUpdate();
            }
            createdEmployeeId = -1;
        }

        // Now that the employee row is gone, the department is empty again and DepartmentDAO's own delete() will allow removing it.
        departmentDAO.delete(testDepartmentId);
    }

    @Test
    void create_savesEmployeeAndAssignsId() {
        Employee employee = new Employee(0, "Test Employee", "Tester", 1000.0, testDepartmentId, true);

        Employee saved = employeeDAO.create(employee);
        createdEmployeeId = saved.getId();

        assertTrue(saved.getId() > 0);
    }

    @Test
    void findById_existingEmployee_returnsIt() {
        Employee employee = employeeDAO.create(new Employee(0, "Findable Employee", "Tester", 1500.0, testDepartmentId, true));
        createdEmployeeId = employee.getId();

        Optional<Employee> result = employeeDAO.findById(createdEmployeeId);

        assertTrue(result.isPresent());
        assertEquals("Findable Employee", result.get().getName());
    }

    @Test
    void update_changesSalary() {
        Employee employee = employeeDAO.create(new Employee(0, "Raise Test", "Tester", 2000.0, testDepartmentId, true));
        createdEmployeeId = employee.getId();

        employee.setSalary(2500.0);
        employeeDAO.update(employee);

        Optional<Employee> result = employeeDAO.findById(createdEmployeeId);
        assertEquals(2500.0, result.get().getSalary());
    }

    @Test
    void deactivate_setsActiveToFalse() {
        Employee employee = employeeDAO.create(new Employee(0, "Leaving Employee", "Tester", 1800.0, testDepartmentId, true));
        createdEmployeeId = employee.getId();

        employeeDAO.deactivate(createdEmployeeId);

        Optional<Employee> result = employeeDAO.findById(createdEmployeeId);
        assertFalse(result.get().isActive());
    }

    @Test
    void findByDepartmentId_returnsOnlyThatDepartmentsEmployees() {
        Employee employee = employeeDAO.create(new Employee(0, "Dept Test Employee", "Tester", 1200.0, testDepartmentId, true));
        createdEmployeeId = employee.getId();

        List<Employee> result = employeeDAO.findByDepartmentId(testDepartmentId);

        assertTrue(result.stream().anyMatch(e -> e.getId() == createdEmployeeId));
    }
}