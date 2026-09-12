package com.budgetms.app;

import com.budgetms.model.Department;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Temporary shared storage so every screen can see the same departments,
 */
public class AppState {

    public static final ObservableList<Department> departments = FXCollections.observableArrayList();
}