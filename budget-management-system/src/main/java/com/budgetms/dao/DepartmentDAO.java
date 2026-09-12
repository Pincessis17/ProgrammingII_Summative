package com.budgetms.dao;

import com.budgetms.model.Department;
import java.util.List;
import java.util.Optional;

// Defines the operations available for persisting and retrieving Department records.

public interface DepartmentDAO {

    Department create(Department department);

    Optional<Department> findById(int id);

    List<Department> findAll();

    void update(Department department);

    void delete(int id);
}
