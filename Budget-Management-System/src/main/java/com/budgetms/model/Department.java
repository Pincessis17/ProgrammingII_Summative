package com.budgetms.model;

import java.util.ArrayList;
import java.util.List;

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
     */
    public double getBudgetVariance() {
        return activeBudget.getAllocatedAmount() - calculateCost();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void addSubDepartment(Department department) {
        subDepartments.add(department);
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
