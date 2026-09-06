package com.budgetms.ui.controller;

import com.budgetms.data.MockDataStore;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Wireframe for the Employee screen. Deliberately no delete button — per the
 * proposal's CRUD scope, employees are create/read/update only; someone who
 * leaves gets unchecked ("Active") rather than removed.
 */
public class EmployeeController {

    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> roleColumn;
    @FXML
    private TableColumn<Employee, String> departmentColumn;
    @FXML
    private TableColumn<Employee, String> salaryColumn;
    @FXML
    private TableColumn<Employee, String> activeColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField roleField;
    @FXML
    private TextField salaryField;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Label validationLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;

    private final MockDataStore store = MockDataStore.getInstance();
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    private Employee selected;

    @FXML
    private void initialize() {
        employees.setAll(store.getAllEmployees());
        employeeTable.setItems(employees);

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        departmentColumn.setCellValueFactory(data -> {
            Department d = store.findDepartmentById(data.getValue().getDepartmentId());
            return new SimpleStringProperty(d == null ? "—" : d.getName());
        });
        salaryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("$%,.2f", data.getValue().getSalary())));
        activeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isActive() ? "Active" : "Inactive"));

        departmentComboBox.setItems(FXCollections.observableArrayList(store.getAllDepartments()));
        departmentComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Department d) {
                return d == null ? "" : d.getName();
            }

            @Override
            public Department fromString(String s) {
                return null;
            }
        });

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            selected = sel;
            populateForm(sel);
        });

        handleClear();
    }

    private void populateForm(Employee e) {
        if (e == null) {
            handleClear();
            return;
        }
        formTitleLabel.setText("Edit Employee");
        nameField.setText(e.getName());
        roleField.setText(e.getRole());
        salaryField.setText(String.valueOf(e.getSalary()));
        departmentComboBox.setValue(store.findDepartmentById(e.getDepartmentId()));
        activeCheckBox.setSelected(e.isActive());
        updateButton.setDisable(false);
        validationLabel.setText("");
    }

    private boolean validateForm() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            validationLabel.setText("Name is required.");
            return false;
        }
        if (departmentComboBox.getValue() == null) {
            validationLabel.setText("Select a department.");
            return false;
        }
        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            if (salary < 0) {
                validationLabel.setText("Salary can't be negative.");
                return false;
            }
        } catch (NumberFormatException ex) {
            validationLabel.setText("Salary must be a number.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) {
            return;
        }
        Department dept = departmentComboBox.getValue();
        Employee e = new Employee(store.nextEmployeeId(), nameField.getText().trim(),
                roleField.getText().trim(), Double.parseDouble(salaryField.getText().trim()),
                dept.getId(), activeCheckBox.isSelected());
        dept.addEmployee(e);
        store.getAllEmployees().add(e);
        employees.setAll(store.getAllEmployees());
        employeeTable.getSelectionModel().select(e);
        handleClear();
    }

    @FXML
    private void handleUpdate() {
        if (selected == null || !validateForm()) {
            return;
        }
        Department newDept = departmentComboBox.getValue();
        if (selected.getDepartmentId() != newDept.getId()) {
            Department oldDept = store.findDepartmentById(selected.getDepartmentId());
            if (oldDept != null) {
                oldDept.getEmployees().remove(selected);
            }
            newDept.addEmployee(selected);
        }
        selected.setName(nameField.getText().trim());
        selected.setRole(roleField.getText().trim());
        selected.setSalary(Double.parseDouble(salaryField.getText().trim()));
        selected.setActive(activeCheckBox.isSelected());
        employeeTable.refresh();
    }

    @FXML
    private void handleClear() {
        selected = null;
        formTitleLabel.setText("Add Employee");
        nameField.clear();
        roleField.clear();
        salaryField.clear();
        departmentComboBox.setValue(null);
        activeCheckBox.setSelected(true);
        updateButton.setDisable(true);
        validationLabel.setText("");
        employeeTable.getSelectionModel().clearSelection();
    }
}
