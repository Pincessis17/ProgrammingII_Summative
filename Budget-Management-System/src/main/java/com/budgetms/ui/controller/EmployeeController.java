package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class EmployeeController {

    @FXML private TextField nameField;
    @FXML private TextField roleField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<Department> departmentPicker;
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> roleColumn;
    @FXML private TableColumn<Employee, Double> salaryColumn;
    @FXML private TableColumn<Employee, Boolean> activeColumn;

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        employeeTable.setItems(employees);

        departmentPicker.setItems(AppState.departments);
        departmentPicker.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department department) {
                return department == null ? "" : department.getName();
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String role = roleField.getText();
        String salaryText = salaryField.getText();

        if (name == null || name.isBlank() || role == null || role.isBlank()) {
            showAlert("Name and role are required.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            showAlert("Salary must be a number.");
            return;
        }

        Employee newEmployee = new Employee(name, role, salary);
        employees.add(newEmployee);

        Department department = departmentPicker.getValue();
        if (department != null) {
            department.addChild(newEmployee);
        }

        nameField.clear();
        roleField.clear();
        salaryField.clear();
        departmentPicker.setValue(null);
    }

    @FXML
    private void handleToggleActive() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select an employee first.");
            return;
        }

        selected.setActive(!selected.isActive());
        employeeTable.refresh();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
