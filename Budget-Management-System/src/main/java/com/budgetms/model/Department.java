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

    private static final int MAX_NESTING_DEPTH = 5;

    private String name;
    private Budget activeBudget;
    private int depth = 0;
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
    public double calculateBudget() {
        double total = 0;

        for (Employee employee : employees) {
            total += employee.calculateBudget();
        }

        for (Department child : subDepartments) {
            total += child.calculateBudget();
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
     */
    public double getBudgetVariance() {
        return activeBudget.getAllocatedAmount() - calculateBudget();
    }

    /**
     * Adds an Employee or a smaller Department underneath this one.
     * Refuses to nest departments more than MAX_NESTING_DEPTH levels deep.
     */
    public void addChild(OrgUnit child) {
        if (child instanceof Employee employee) {
            employees.add(employee);
        } else if (child instanceof Department department) {
            if (this.depth + 1 > MAX_NESTING_DEPTH) {
                throw new IllegalStateException(
                        "Can't nest departments more than " + MAX_NESTING_DEPTH + " levels deep.");
            }
            department.depth = this.depth + 1;
            subDepartments.add(department);
        }
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
