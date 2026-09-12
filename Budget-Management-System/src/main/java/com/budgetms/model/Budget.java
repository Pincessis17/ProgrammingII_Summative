package com.budgetms.model;

public class Budget {

    private double allocatedAmount;
    private String period;

    public Budget(double allocatedAmount, String period) {
        this.allocatedAmount = allocatedAmount;
        this.period = period;
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
