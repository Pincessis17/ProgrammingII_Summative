package com.budgetms.model;

public class Budget {

    private int id;
    private int departmentId;
    private String period;
    private double allocatedAmount;

    /**
     * Used when loading a budget that already exists in the database.
     */
    public Budget(int id, int departmentId, String period, double allocatedAmount) {
        this.id = id;
        this.departmentId = departmentId;
        this.period = period;
        this.allocatedAmount = allocatedAmount;
    }

    /**
     * Used when setting a brand-new budget from the UI, before it's been
     * saved. It isn't linked to a department yet either — that happens
     * via Department.setActiveBudget(), which sets the department ID.
     */
    public Budget(double allocatedAmount, String period) {
        this(0, 0, period, allocatedAmount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}
