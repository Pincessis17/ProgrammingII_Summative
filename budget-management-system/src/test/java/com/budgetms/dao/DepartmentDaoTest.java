package com.budgetms.dao;

import com.budgetms.model.Department;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

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
}