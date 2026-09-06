package com.budgetms.model;

// Represents an employee within the organisation.
// An employee has personal information, a role, salary, department information, and an active/inactive status.
public class Employee implements OrgUnit {

    // Unique identifier for the employee.
    private int id;

    // Name of the employee.
    private String name;

    // Job role or position of the employee.
    private String role;

    // Salary of the employee.
    private double salary;

    // ID of the department the employee belongs to.
    private int departmentId;

    // Indicates whether the employee is currently active.
    private boolean active;


    // Creates a new Employee object.
    // All employee information is provided when the object is created.
    public Employee(int id, String name, String role, double salary, int departmentId, boolean active) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.salary = salary;
        this.departmentId = departmentId;
        this.active = active;
    }


    // Returns the unique ID of the employee.
    public int getId() {
        return id;
    }

    // Changes the employee's ID.
    public void setId(int id) {
        this.id = id;
    }


    // Returns the employee's name.
    // @Override is used because getName() is defined in OrgUnit.
    @Override
    public String getName() {
        return name;
    }

    // Changes the employee's name.
    public void setName(String name) {
        this.name = name;
    }


    // Returns the employee's job role.
    public String getRole() {
        return role;
    }

    // Changes the employee's job role.
    public void setRole(String role) {
        this.role = role;
    }


    // Returns the employee's salary.
    public double getSalary() {
        return salary;
    }

    // Changes the employee's salary.
    public void setSalary(double salary) {
        this.salary = salary;
    }


    // Returns the ID of the department the employee belongs to.
    public int getDepartmentId() {
        return departmentId;
    }

    // Changes the department ID assigned to the employee.
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }


    // Returns true if the employee is currently active.
    public boolean isActive() {
        return active;
    }

    // Changes the employee's active status.
    public void setActive(boolean active) {
        this.active = active;
    }


    // Calculates the employee's cost to the organisation.
    // An active employee contributes their full salary.
    // An inactive employee contributes zero cost.
    @Override
    public double calculateCost() {
        return active ? salary : 0.0;
    }


    // Calculates the employee's contribution to the headcount.
    // An active employee counts as 1.
    // An inactive employee counts as 0.
    @Override
    public int getHeadcount() {
        return active ? 1 : 0;
    }
}