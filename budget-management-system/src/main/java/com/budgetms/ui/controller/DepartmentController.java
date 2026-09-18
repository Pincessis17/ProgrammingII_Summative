package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.dao.DepartmentDAO;
import com.budgetms.dao.DepartmentDAOImpl;
import com.budgetms.dao.DepartmentNotEmptyException;
import com.budgetms.model.Department;
import com.budgetms.util.ValidationUtils;
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
    @FXML private TableColumn<Department, String> parentColumn;

    private final ObservableList<Department> departments = AppState.departments;
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> {
            Department dept = cellData.getValue();
            String indent = "    ".repeat(dept.getDepth());
            return new javafx.beans.property.SimpleStringProperty(indent + dept.getName());
        });
        parentColumn.setCellValueFactory(cellData -> {
            Department parent = cellData.getValue().getParent();
            return new javafx.beans.property.SimpleStringProperty(parent == null ? "—" : parent.getName());
        });
        headcountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getHeadcount()).asObject());
        costColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().calculateBudget()).asObject());

        departmentTable.setItems(departments);

        departmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
            }
        });

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

        try {
            ValidationUtils.requireNonBlank(name, "Department name");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
            return;
        }

        Department newDepartment = new Department(name);

        Department parent = parentPicker.getValue();
        if (parent != null) {
            try {
                parent.addChild(newDepartment);
            } catch (IllegalStateException e) {
                showAlert(e.getMessage());
                return;
            }
        }

        departmentDAO.create(newDepartment);
        departments.add(newDepartment);

        nameField.clear();
        parentPicker.setValue(null);
    }

    @FXML
    private void handleUpdate() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select a department to update first.");
            return;
        }

        String name = nameField.getText();
        try {
            ValidationUtils.requireNonBlank(name, "Department name");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
            return;
        }

        selected.setName(name);

        Department newParent = parentPicker.getValue();
        Department oldParent = selected.getParent();

        if (newParent == selected) {
            showAlert("A department cannot be its own parent.");
            return;
        }

        if (newParent != null && selected.isAncestorOf(newParent)) {
            showAlert("Can't move a department under one of its own sub-departments.");
            return;
        }

        if (newParent != oldParent) {
            if (oldParent != null) {
                oldParent.removeChild(selected);
            }
            try {
                if (newParent != null) {
                    newParent.addChild(selected);
                } else {
                    selected.setParent(null);
                }
            } catch (IllegalStateException e) {
                if (oldParent != null) {
                    oldParent.addChild(selected); // move failed — put it back
                }
                showAlert(e.getMessage());
                return;
            }
        }

        departmentDAO.update(selected);
        departmentTable.refresh();

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

        if (!selected.getEmployees().isEmpty() || !selected.getChildren().isEmpty()) {
            showAlert("Can't delete a department that still has employees or sub-departments.");
            return;
        }

        try {
            departmentDAO.delete(selected.getId());
        } catch (DepartmentNotEmptyException e) {
            showAlert(e.getMessage());
            return;
        }

        departments.remove(selected);
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
