package com.budgetms.model;

import java.util.ArrayList;
import java.util.List;

// Represents a department within the organisation.
// A department can have a parent department, child departments, employees, and an active budget.
public class Department implements OrgUnit {

    // Unique identifier for the department.
    private int id;

    // Name of the department.
    private String name;

    // The parent department of this department.
    // This is null if the department is at the top level.
    private Department parent;

    // List of departments directly below this department.
    private final List<Department> children = new ArrayList<>();

    // List of employees who belong directly to this department.
    private final List<Employee> employees = new ArrayList<>();

    // The budget currently assigned to this department.
    private Budget activeBudget;


    // Creates a new Department object.
    // The department is initialized with an ID, name, and parent department.
    public Department(int id, String name, Department parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }


    // Returns the unique ID of the department.
    public int getId() {
        return id;
    }

    // Changes the ID of the department.
    public void setId(int id) {
        this.id = id;
    }


    // Returns the name of the department.
    // @Override is used because getName() is defined in OrgUnit.
    @Override
    public String getName() {
        return name;
    }

    // Changes the name of the department.
    public void setName(String name) {
        this.name = name;
    }


    // Returns the parent department.
    public Department getParent() {
        return parent;
    }

    // Changes the parent department.
    public void setParent(Department parent) {
        this.parent = parent;
    }


    // Returns the list of child departments.
    public List<Department> getChildren() {
        return children;
    }

    // Adds a child department to this department.
    // The child's parent is also automatically set to this department.
    public void addChild(Department child) {
        child.setParent(this);
        children.add(child);
    }


    // Returns the list of employees belonging to this department.
    public List<Employee> getEmployees() {
        return employees;
    }

    // Adds an employee to this department.
    // The employee's department ID is automatically set to this department's ID.
    public void addEmployee(Employee employee) {
        employee.setDepartmentId(this.id);
        employees.add(employee);
    }


    // Returns the budget currently assigned to this department.
    public Budget getActiveBudget() {
        return activeBudget;
    }

    // Assigns a budget as the department's active budget.
    public void setActiveBudget(Budget activeBudget) {
        this.activeBudget = activeBudget;
    }


    // Calculates the total cost of this department.
    // This includes the cost of employees directly in this department as well as the costs of all child departments.
    @Override
    public double calculateCost() {
        double total = 0.0;

        // Add the cost of each employee in this department.
        for (Employee e : employees) {
            total += e.calculateCost();
        }

        // Add the cost of each child department.
        // Each child department also calculates its own employees and child departments recursively.
        for (Department child : children) {
            total += child.calculateCost();
        }

        return total;
    }


    // Calculates the total number of employees in this department and all of its child departments.
    @Override
    public int getHeadcount() {
        int total = 0;

        // Add the headcount of employees directly in this department.
        for (Employee e : employees) {
            total += e.getHeadcount();
        }

        // Add the headcount from each child department.
        // This is calculated recursively for nested departments.
        for (Department child : children) {
            total += child.getHeadcount();
        }

        return total;
    }


    // Calculates the difference between the allocated budget and the actual cost of the department.
    // A positive result means money remains in the budget.
    // A negative result means the department has exceeded its budget.
    public double getBudgetVariance() {

        // If there is no active budget, there is no variance to calculate.
        if (activeBudget == null) {
            return 0.0;
        }

        // Subtract the actual department cost from the allocated budget.
        return activeBudget.getAllocatedAmount() - calculateCost();
    }
}