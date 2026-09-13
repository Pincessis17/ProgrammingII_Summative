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

    // How many levels deep departments can nest, counting the top level
    // as depth 1. Keeps calculateBudget()/getHeadcount() recursion bounded.
    private static final int MAX_DEPTH = 6;

    private int id;
    private String name;
    private Department parent;
    private Budget activeBudget;
    private final List<Employee> employees = new ArrayList<>();
    private final List<Department> children = new ArrayList<>();

    /**
     * Used when loading a department that already exists in the database.
     */
    public Department(int id, String name, Department parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    /**
     * Used when creating a brand-new top-level department from the UI,
     * before it's been saved.
     */
    public Department(String name) {
        this(0, name, null);
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

    public Department getParent() {
        return parent;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }

    /**
     * How deep this department sits in the hierarchy. A top-level
     * department (no parent) is depth 1.
     */
    private int getDepth() {
        int depth = 1;
        Department current = this.parent;
        while (current != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }

    /**
     * Adds a smaller department underneath this one, and sets its parent
     * to this department. Refuses to nest departments more than
     * MAX_DEPTH levels deep.
     */
    public void addChild(Department child) {
        if (this.getDepth() + 1 > MAX_DEPTH) {
            throw new IllegalStateException(
                    "Cannot add child department: maximum nesting depth of " + MAX_DEPTH + " exceeded.");
        }
        child.setParent(this);
        children.add(child);
    }

    public List<Department> getChildren() {
        return children;
    }

    /**
     * Adds an employee to this department, and sets the employee's
     * department ID to match.
     */
    public void addEmployee(Employee employee) {
        employee.setDepartmentId(this.id);
        employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public Budget getActiveBudget() {
        return activeBudget;
    }

    /**
     * Assigns a budget as this department's active budget, and sets the
     * budget's department ID to match.
     */
    public void setActiveBudget(Budget activeBudget) {
        if (activeBudget != null) {
            activeBudget.setDepartmentId(this.id);
        }
        this.activeBudget = activeBudget;
    }

    @Override
    public double calculateBudget() {
        double total = 0;

        for (Employee employee : employees) {
            total += employee.calculateBudget();
        }

        for (Department child : children) {
            total += child.calculateBudget();
        }

        return total;
    }

    @Override
    public int getHeadcount() {
        int total = 0;

        for (Employee employee : employees) {
            total += employee.getHeadcount();
        }

        for (Department child : children) {
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
        return activeBudget.getAllocatedAmount() - calculateBudget();
    }
}
