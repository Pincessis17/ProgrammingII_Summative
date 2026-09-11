package com.budgetms.model;

/**
 * A single employee. An employee always counts as 1 person, and their
 * cost is just their salary — no adding anything up.
 */
public class Employee implements OrgUnit {

    private String name;
    private String role;
    private double salary;
    private boolean active;

    public Employee(String name, String role, double salary) {
        this.name = name;
        this.role = role;
        this.salary = salary;
        this.active = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double calculateBudget() {
        return salary;
    }

    @Override
    public int getHeadcount() {
        return 1;
    }

    // Plain getters/setters — just reading and changing values, no logic.

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
