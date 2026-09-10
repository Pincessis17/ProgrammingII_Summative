package com.budgetms.model;

// Defines the common behaviour that all organisational units must have.
// Both Department and Employee implement this interface.
public interface OrgUnit {

    // Returns the name of the organisational unit.
    String getName();

    // Calculates the total cost associated with the organisational unit.
    double calculateBudget();

    // Returns the number of active employees represented by the unit.
    int getHeadcount();
}