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
import javafx.scene.control.ButtonType;

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

        departmentPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                amountField.clear();
                periodField.clear();
                return;
            }
            Optional<Budget> existing;
            try {
                existing = budgetDAO.findByDepartmentId(newVal.getId());
            } catch (RuntimeException e) {
                existing = Optional.empty();
            }
            if (existing.isPresent()) {
                amountField.setText(String.valueOf(existing.get().getAllocatedAmount()));
                periodField.setText(existing.get().getPeriod());
            } else {
                amountField.clear();
                periodField.clear();
            }
        });
    }

    @FXML
    private void handleDeleteBudget() {
        Department selected = departmentPicker.getValue();

        if (selected == null) {
            showAlert("Choose a department first.");
            return;
        }

        try {
            Optional<Budget> existing = budgetDAO.findByDepartmentId(selected.getId());

            if (existing.isEmpty()) {
                showAlert("This department has no budget to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete the budget for " + selected.getName() + "? This action can't be undone.",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("Delete budget");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }

            budgetDAO.delete(existing.get().getId());
        } catch (RuntimeException e) {
            e.printStackTrace();
            showAlert("Couldn't delete this budget. Check that the database is running, then try again.");
            return;
        }
        selected.setActiveBudget(null);
        amountField.clear();
        periodField.clear();

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
        budget.setDepartmentId(selected.getId());

        try {
            Optional<Budget> existing = budgetDAO.findByDepartmentId(selected.getId());
            if (existing.isPresent()) {
                budget.setId(existing.get().getId());
                budgetDAO.update(budget);
            } else {
                budgetDAO.create(budget);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            showAlert("Couldn't save this budget. Check that the database is running, then try again.");
            return;
        }
        selected.setActiveBudget(budget);

        budgetTable.refresh();

        amountField.clear();
        periodField.clear();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
