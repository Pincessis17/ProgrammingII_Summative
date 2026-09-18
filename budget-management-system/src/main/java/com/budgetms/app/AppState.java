package com.budgetms.app;

import com.budgetms.dao.EmployeeDAO;
import com.budgetms.dao.EmployeeDAOImpl;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {

    public static final ObservableList<Department> departments = FXCollections.observableArrayList();

    private static final EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    public static void refreshEmployees() {
        for (Department department : departments) {
            department.getEmployees().clear();
            department.getEmployees().addAll(employeeDAO.findByDepartmentId(department.getId()));
        }
    }
}