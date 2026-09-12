package com.budgetms.model;

/**
 * A single employee. Cost and headcount both depend on whether the
 * employee is currently active — an inactive employee is kept on record
 * (so past salary/headcount history isn't lost) but no longer counts
 * toward cost or headcount.
 */
public class Employee implements OrgUnit {

    private int id;
    private String name;
    private String role;
    private double salary;
    private int departmentId;
    private boolean active;

    /**
     * Used when loading an employee that already exists in the database.
     */
    public Employee(int id, String name, String role, double salary, int departmentId, boolean active) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.salary = salary;
        this.departmentId = departmentId;
        this.active = active;
    }

    /**
     * Used when creating a brand-new employee from the UI, before it has
     * been saved. It isn't assigned to a department yet either — that
     * happens via Department.addEmployee(), which sets the department ID.
     */
    public Employee(String name, String role, double salary) {
        this(0, name, role, salary, 0, true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

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

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public double calculateBudget() {
        return active ? salary : 0.0;
    }

    @Override
    public int getHeadcount() {
        return active ? 1 : 0;
    }
}
