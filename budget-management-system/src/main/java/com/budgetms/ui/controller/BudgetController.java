package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.dao.BudgetDAO;
import com.budgetms.dao.BudgetDAOImpl;
import com.budgetms.model.Budget;
import com.budgetms.model.Department;
import com.budgetms.util.ValidationUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import com.budgetms.util.CurrencyFormatter;

import java.util.Optional;

public class BudgetController {

    @FXML private ComboBox<Department> departmentPicker;
    @FXML private TextField amountField;
    @FXML private TextField periodField;
    @FXML private TableView<Department> budgetTable;
    @FXML private TableColumn<Department, String> departmentColumn;
    @FXML private TableColumn<Department, Double> allocatedColumn;
    @FXML private TableColumn<Department, Double> actualColumn;
    @FXML private TableColumn<Department, Double> varianceColumn;

    private final BudgetDAO budgetDAO = new BudgetDAOImpl();

    @FXML
    public void initialize() {
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

        departmentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        allocatedColumn.setCellValueFactory(cellData -> {
            Budget budget = cellData.getValue().getActiveBudget();
            return new SimpleDoubleProperty(budget == null ? 0 : budget.getAllocatedAmount()).asObject();
        });

        allocatedColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Department, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : CurrencyFormatter.format(value));
            }
        });

        actualColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().calculateBudget()).asObject());

        actualColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Department, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : CurrencyFormatter.format(value));
            }
        });

        varianceColumn.setCellValueFactory(cellData -> {
            Department department = cellData.getValue();
            double variance = department.getActiveBudget() == null ? 0 : department.getBudgetVariance();
            return new SimpleDoubleProperty(variance).asObject();
        });

        varianceColumn.setCellFactory(col -> new javafx.scene.control.TableCell<Department, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : CurrencyFormatter.format(value));
            }
        });

        budgetTable.setItems(AppState.departments);
    }

    @FXML
    private void handleDeleteBudget() {
        Department selected = departmentPicker.getValue();

        if (selected == null) {
            showAlert("Choose a department first.");
            return;
        }

        Optional<Budget> existing = budgetDAO.findByDepartmentId(selected.getId());

        if (existing.isEmpty()) {
            showAlert("This department has no budget to delete.");
            return;
        }

        budgetDAO.delete(existing.get().getId());
        selected.setActiveBudget(null);

        budgetTable.refresh();
    }

    @FXML
    private void handleSetBudget() {
        Department selected = departmentPicker.getValue();

        if (selected == null) {
            showAlert("Choose a department first.");
            return;
        }

        String period = periodField.getText();
        double amount;

        try {
            ValidationUtils.requireNonBlank(period, "Period");
            amount = ValidationUtils.requirePositiveNumber(amountField.getText(), "Allocated amount");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
            return;
        }

        Budget budget = new Budget(amount, period);
        selected.setActiveBudget(budget);

        Optional<Budget> existing = budgetDAO.findByDepartmentId(selected.getId());
        if (existing.isPresent()) {
            budget.setId(existing.get().getId());
            budgetDAO.update(budget);
        } else {
            budgetDAO.create(budget);
        }

        budgetTable.refresh();

        amountField.clear();
        periodField.clear();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
