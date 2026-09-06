package com.budgetms.ui.controller;

import com.budgetms.data.MockDataStore;
import com.budgetms.model.Budget;
import com.budgetms.model.Department;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Wireframe for the Budget screen. "Actual Cost" and "Variance" are read-only,
 * computed columns — they call straight into Department.calculateCost() /
 * getBudgetVariance(), which is where the real recursive algorithm (Day 5)
 * will slot in without this screen needing to change.
 */
public class BudgetController {

    @FXML
    private TableView<Budget> budgetTable;
    @FXML
    private TableColumn<Budget, String> departmentColumn;
    @FXML
    private TableColumn<Budget, String> periodColumn;
    @FXML
    private TableColumn<Budget, String> allocatedColumn;
    @FXML
    private TableColumn<Budget, String> actualCostColumn;
    @FXML
    private TableColumn<Budget, String> varianceColumn;

    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private TextField periodField;
    @FXML
    private TextField allocatedField;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Label validationLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private final MockDataStore store = MockDataStore.getInstance();
    private final ObservableList<Budget> budgets = FXCollections.observableArrayList();

    private Budget selected;

    @FXML
    private void initialize() {
        budgets.setAll(store.getAllBudgets());
        budgetTable.setItems(budgets);

        departmentColumn.setCellValueFactory(data -> {
            Department d = store.findDepartmentById(data.getValue().getDepartmentId());
            return new SimpleStringProperty(d == null ? "—" : d.getName());
        });
        periodColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPeriod()));
        allocatedColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("$%,.2f", data.getValue().getAllocatedAmount())));
        actualCostColumn.setCellValueFactory(data -> {
            Department d = store.findDepartmentById(data.getValue().getDepartmentId());
            return new SimpleStringProperty(d == null ? "—" : String.format("$%,.2f", d.calculateCost()));
        });
        varianceColumn.setCellValueFactory(data -> {
            Department d = store.findDepartmentById(data.getValue().getDepartmentId());
            double variance = (d == null) ? 0
                    : data.getValue().getAllocatedAmount() - d.calculateCost();
            return new SimpleStringProperty(String.format("%+,.2f", variance));
        });
        varianceColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    getStyleClass().removeAll("variance-positive", "variance-negative");
                } else {
                    setText(value);
                    getStyleClass().removeAll("variance-positive", "variance-negative");
                    getStyleClass().add(value.startsWith("-") ? "variance-negative" : "variance-positive");
                }
            }
        });

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

        budgetTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            selected = sel;
            populateForm(sel);
        });

        handleClear();
    }

    private void populateForm(Budget b) {
        if (b == null) {
            handleClear();
            return;
        }
        formTitleLabel.setText("Edit Budget");
        departmentComboBox.setValue(store.findDepartmentById(b.getDepartmentId()));
        periodField.setText(b.getPeriod());
        allocatedField.setText(String.valueOf(b.getAllocatedAmount()));
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        validationLabel.setText("");
    }

    private boolean validateForm() {
        if (departmentComboBox.getValue() == null) {
            validationLabel.setText("Select a department.");
            return false;
        }
        if (periodField.getText() == null || periodField.getText().trim().isEmpty()) {
            validationLabel.setText("Period is required (e.g. 2026-Q4).");
            return false;
        }
        try {
            double amount = Double.parseDouble(allocatedField.getText().trim());
            if (amount < 0) {
                validationLabel.setText("Allocated amount can't be negative.");
                return false;
            }
        } catch (NumberFormatException ex) {
            validationLabel.setText("Allocated amount must be a number.");
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
        Budget b = new Budget(store.nextBudgetId(), dept.getId(), periodField.getText().trim(),
                Double.parseDouble(allocatedField.getText().trim()));
        dept.setActiveBudget(b);
        store.getAllBudgets().add(b);
        budgets.setAll(store.getAllBudgets());
        budgetTable.getSelectionModel().select(b);
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
            if (oldDept != null && oldDept.getActiveBudget() == selected) {
                oldDept.setActiveBudget(null);
            }
            selected.setDepartmentId(newDept.getId());
            newDept.setActiveBudget(selected);
        }
        selected.setPeriod(periodField.getText().trim());
        selected.setAllocatedAmount(Double.parseDouble(allocatedField.getText().trim()));
        budgetTable.refresh();
    }

    @FXML
    private void handleDelete() {
        if (selected == null) {
            return;
        }
        Department dept = store.findDepartmentById(selected.getDepartmentId());
        if (dept != null && dept.getActiveBudget() == selected) {
            dept.setActiveBudget(null);
        }
        store.getAllBudgets().remove(selected);
        budgets.setAll(store.getAllBudgets());
        handleClear();
    }

    @FXML
    private void handleClear() {
        selected = null;
        formTitleLabel.setText("Add Budget");
        departmentComboBox.setValue(null);
        periodField.clear();
        allocatedField.clear();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        validationLabel.setText("");
        budgetTable.getSelectionModel().clearSelection();
    }
}
