package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.dao.EmployeeDAO;
import com.budgetms.dao.EmployeeDAOImpl;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import com.budgetms.util.ValidationUtils;
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
import com.budgetms.util.CurrencyFormatter;

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
    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Employee, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : CurrencyFormatter.format(value));
            }
        });
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        employees.setAll(employeeDAO.findAll());
        employeeTable.setItems(employees);

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                roleField.setText(newSelection.getRole());
                salaryField.setText(String.valueOf(newSelection.getSalary()));
                departmentPicker.setValue(findDepartmentById(newSelection.getDepartmentId()));
            }
        });

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

        double salary;
        try {
            ValidationUtils.requireNonBlank(name, "Name");
            ValidationUtils.requireNonBlank(role, "Role");
            salary = ValidationUtils.requirePositiveNumber(salaryText, "Salary");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
            return;
        }

        Employee newEmployee = new Employee(name, role, salary);

        Department department = departmentPicker.getValue();
        if (department != null) {
            department.addEmployee(newEmployee);
        }

        employeeDAO.create(newEmployee);
        employees.add(newEmployee);

        nameField.clear();
        roleField.clear();
        salaryField.clear();
        departmentPicker.setValue(null);
    }

    @FXML
    private void handleUpdate() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select an employee first.");
            return;
        }

        String name = nameField.getText();
        String role = roleField.getText();
        String salaryText = salaryField.getText();

        double salary;
        try {
            ValidationUtils.requireNonBlank(name, "Name");
            ValidationUtils.requireNonBlank(role, "Role");
            salary = ValidationUtils.requirePositiveNumber(salaryText, "Salary");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
            return;
        }

        selected.setName(name);
        selected.setRole(role);
        selected.setSalary(salary);

        Department oldDepartment = findDepartmentById(selected.getDepartmentId());
        Department newDepartment = departmentPicker.getValue();

        if (newDepartment != null && newDepartment != oldDepartment) {
            if (oldDepartment != null) {
                oldDepartment.removeEmployee(selected);
            }
            selected.setDepartmentId(newDepartment.getId());
            newDepartment.addEmployee(selected);
        }

        employeeDAO.update(selected);
        employeeTable.refresh();

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

        if (selected.isActive()) {
            employeeDAO.deactivate(selected.getId());
            selected.setActive(false);
        } else {
            selected.setActive(true);
            employeeDAO.update(selected);
        }

        employeeTable.refresh();
    }

    private Department findDepartmentById(int id) {
        for (Department d : AppState.departments) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
