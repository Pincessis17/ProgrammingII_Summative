package com.budgetms.model;

/**
 * Anything that can be part of the org chart implements this —
 * a Department and an Employee both count as one.
 */
public interface OrgUnit {

    /**
     * The name to show for this — a department's name, or a person's name.
     */
    String getName();

    /**
     * How much this costs in total.
     * An employee just returns their own cost.
     * A department returns its own budget plus everything under it added up.
     */
    double calculateBudget();

    /**
     * How many people this counts as.
     * An employee is always 1.
     * A department is everyone inside it, including people in smaller
     * departments underneath it.
     */
    int getHeadcount();
}
