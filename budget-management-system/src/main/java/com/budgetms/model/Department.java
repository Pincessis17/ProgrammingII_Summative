package com.budgetms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A department. It can hold its own employees, and it can also hold
 * smaller departments underneath it — that's what lets the whole
 * company be built as one tree (e.g. Faculty of Computing containing
 * CS and IT).
 */
public class Department implements OrgUnit {

    private String name;
    private Budget activeBudget;
    private final List<Employee> employees = new ArrayList<>();
    private final List<Department> subDepartments = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double calculateCost() {
        double total = 0;

        for (Employee employee : employees) {
            total += employee.calculateCost();
        }

        for (Department child : subDepartments) {
            total += child.calculateCost();
        }

        return total;
    }

    @Override
    public int getHeadcount() {
        int total = employees.size();

        for (Department child : subDepartments) {
            total += child.getHeadcount();
        }

        return total;
    }

    /**
     * How much of the budget is left, or how far over it this
     * department has gone.
     * Positive number = under budget. Negative number = over budget.
     * Returns 0 if no budget has been set yet, so it's safe to call
     * before a budget exists.
     */
    public double getBudgetVariance() {
        if (activeBudget == null) {
            return 0.0;
        }
        return activeBudget.getAllocatedAmount() - calculateCost();
    }

    /**
     * Adds a smaller department underneath this one.
     */
    public void addChild(Department child) {
        subDepartments.add(child);
    }

    /**
     * Adds an employee to this department.
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Department> getSubDepartments() {
        return subDepartments;
    }

    public Budget getActiveBudget() {
        return activeBudget;
    }

    public void setActiveBudget(Budget activeBudget) {
        this.activeBudget = activeBudget;
    }

    public void setName(String name) {
        this.name = name;
    }
}
