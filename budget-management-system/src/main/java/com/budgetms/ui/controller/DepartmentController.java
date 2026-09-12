package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.model.Department;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class DepartmentController {

    @FXML private TextField nameField;
    @FXML private ComboBox<Department> parentPicker;
    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, String> nameColumn;
    @FXML private TableColumn<Department, Integer> headcountColumn;
    @FXML private TableColumn<Department, Double> costColumn;

    private final ObservableList<Department> departments = AppState.departments;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        headcountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getHeadcount()).asObject());
        costColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().calculateCost()).asObject());

        departmentTable.setItems(departments);

        parentPicker.setItems(AppState.departments);
        parentPicker.setConverter(new StringConverter<Department>() {
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

        if (name == null || name.isBlank()) {
            showAlert("Please enter a department name.");
            return;
        }

        Department newDepartment = new Department(name);
        departments.add(newDepartment);

        Department parent = parentPicker.getValue();
        if (parent != null) {
            parent.addChild(newDepartment);
        }

        nameField.clear();
        parentPicker.setValue(null);
    }

    @FXML
    private void handleDelete() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select a department to delete first.");
            return;
        }

        if (!selected.getEmployees().isEmpty() || !selected.getSubDepartments().isEmpty()) {
            showAlert("Can't delete a department that still has employees or sub-departments.");
            return;
        }

        departments.remove(selected);
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
