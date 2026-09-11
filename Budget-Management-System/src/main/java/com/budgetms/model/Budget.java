package com.budgetms.model;

public class Budget {

    private String category;
    private double allocatedAmount;
    private String period;

    public Budget(String category, double allocatedAmount, String period) {
        this.category = category;
        this.allocatedAmount = allocatedAmount;
        this.period = period;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
