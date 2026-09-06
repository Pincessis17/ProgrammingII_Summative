package com.budgetms.model;

// Represents a single budget allocation record.
// A budget specifies how much money is allocated to a department for a particular time period.

public class Budget {

    // Unique identifier for a budget record.
    private int id;

    // ID of the department that a budget belongs to.
    private int departmentId;

    // The time period covered by a budget.
    private String period;

    // The amount of money allocated to the department.
    private double allocatedAmount;

    // Constructor used to create a Budget object.
    // It receives all four pieces of information needed to create a complete budget record.
    public Budget(int id, int departmentId, String period, double allocatedAmount) {
        this.id = id;
        this.departmentId = departmentId;
        this.period = period;
        this.allocatedAmount = allocatedAmount;
    }


    // Returns the unique ID of this budget.
    public int getId() {
        return id;
    }

    // Changes the ID of a budget.
    public void setId(int id) {
        this.id = id;
    }


    // Returns the ID of the department that a budget belongs to.
    public int getDepartmentId() {
        return departmentId;
    }

    // Changes the department associated with a budget.
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }


    // Returns the time period covered by the budget.
    public String getPeriod() {
        return period;
    }

    // Changes the time period of the budget.
    public void setPeriod(String period) {
        this.period = period;
    }


    // Returns the amount of money allocated to the department.
    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    // Changes the allocated budget amount.
    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}