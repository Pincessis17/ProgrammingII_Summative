package com.budgetms.dao;

import com.budgetms.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {

    Employee create(Employee employee);

    Optional<Employee> findById(int id);

    List<Employee> findAll();

    List<Employee> findByDepartmentId(int departmentId);

    void update(Employee employee);

    void deactivate(int id);
}
