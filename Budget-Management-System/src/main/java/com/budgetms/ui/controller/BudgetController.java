package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.model.Budget;
import com.budgetms.model.Department;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class BudgetController {

    @FXML private ComboBox<Department> departmentPicker;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private TextField periodField;
    @FXML private TableView<Department> budgetTable;
    @FXML private TableColumn<Department, String> departmentColumn;
    @FXML private TableColumn<Department, String> categoryColumn;
    @FXML private TableColumn<Department, Double> allocatedColumn;
    @FXML private TableColumn<Department, Double> actualColumn;
    @FXML private TableColumn<Department, Double> varianceColumn;

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

        categoryColumn.setCellValueFactory(cellData -> {
            Budget budget = cellData.getValue().getActiveBudget();
            return new SimpleStringProperty(budget == null ? "-" : budget.getCategory());
        });

        allocatedColumn.setCellValueFactory(cellData -> {
            Budget budget = cellData.getValue().getActiveBudget();
            return new SimpleDoubleProperty(budget == null ? 0 : budget.getAllocatedAmount()).asObject();
        });

        actualColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().calculateBudget()).asObject());

        varianceColumn.setCellValueFactory(cellData -> {
            Department department = cellData.getValue();
            double variance = department.getActiveBudget() == null ? 0 : department.getBudgetVariance();
            return new SimpleDoubleProperty(variance).asObject();
        });

        budgetTable.setItems(AppState.departments);
    }

    @FXML
    private void handleSetBudget() {
        Department selected = departmentPicker.getValue();

        if (selected == null) {
            showAlert("Choose a department first.");
            return;
        }

        String category = categoryField.getText();
        String period = periodField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Allocated amount must be a number.");
            return;
        }

        selected.setActiveBudget(new Budget(category, amount, period));
        budgetTable.refresh();

        categoryField.clear();
        amountField.clear();
        periodField.clear();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
