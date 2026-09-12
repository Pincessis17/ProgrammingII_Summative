package com.budgetms.dao;

import com.budgetms.model.Budget;
import java.util.List;
import java.util.Optional;

public interface BudgetDAO {

    Budget create(Budget budget);

    Optional<Budget> findById(int id);

    List<Budget> findAll();

    Optional<Budget> findByDepartmentId(int departmentId);

    void update(Budget budget);

    void delete(int id);
}