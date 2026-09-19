package com.budgetms.dao;

import com.budgetms.model.Department;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.budgetms.db.DatabaseConnectionManager;
import com.budgetms.model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentDaoTest {

    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    // Tracks ids created during a test, so we can clean them up afterward.
    private int createdId = -1;

    @AfterEach
    void cleanUp() {
        if (createdId != -1) {
            departmentDAO.delete(createdId);
            createdId = -1;
        }
    }

    @Test
    void create_savesDepartmentAndAssignsId() {
        Department department = new Department(0, "Test Department", null);

        Department saved = departmentDAO.create(department);
        createdId = saved.getId();

        assertTrue(saved.getId() > 0);
    }

    @Test
    void findById_existingDepartment_returnsIt() {
        Department department = departmentDAO.create(new Department(0, "Findable Department", null));
        createdId = department.getId();

        Optional<Department> result = departmentDAO.findById(createdId);

        assertTrue(result.isPresent());
        assertEquals("Findable Department", result.get().getName());
    }

    @Test
    void findById_nonExistentId_returnsEmpty() {
        Optional<Department> result = departmentDAO.findById(999999);

        assertTrue(result.isEmpty());
    }

    @Test
    void update_changesName() {
        Department department = departmentDAO.create(new Department(0, "Old Name", null));
        createdId = department.getId();

        department.setName("New Name");
        departmentDAO.update(department);

        Optional<Department> result = departmentDAO.findById(createdId);
        assertEquals("New Name", result.get().getName());
    }

    @Test
    void delete_emptyDepartment_removesIt() {
        Department department = departmentDAO.create(new Department(0, "To Delete", null));
        int id = department.getId();

        departmentDAO.delete(id);

        Optional<Department> result = departmentDAO.findById(id);
        assertTrue(result.isEmpty());
        // No need to set createdId here — we already deleted it ourselves
    }

    @Test
    void delete_departmentWithEmployees_throwsDepartmentNotEmptyException() throws SQLException {
        Department department = departmentDAO.create(new Department(0, "Has Employees", null));
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();
        Employee employee = employeeDAO.create(
                new Employee(0, "Blocking Employee", "Tester", 1000.0, department.getId(), true));

        assertThrows(DepartmentNotEmptyException.class, () -> departmentDAO.delete(department.getId()));

        // Clean up manually: EmployeeDAO deliberately has no delete(), so remove the
        // test employee directly before the now-empty department can be deleted.
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM employee WHERE id = ?")) {
            stmt.setInt(1, employee.getId());
            stmt.executeUpdate();
        }
        departmentDAO.delete(department.getId());
    }

    @Test
    void delete_departmentWithChildDepartment_throwsDepartmentNotEmptyException() {
        Department parent = departmentDAO.create(new Department(0, "Parent With Child", null));
        Department child = departmentDAO.create(new Department(0, "Blocking Child", parent));

        assertThrows(DepartmentNotEmptyException.class, () -> departmentDAO.delete(parent.getId()));

        // Clean up manually: child must be deleted before parent.
        departmentDAO.delete(child.getId());
        departmentDAO.delete(parent.getId());
    }
}