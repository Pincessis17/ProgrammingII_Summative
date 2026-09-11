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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Wireframe for the Department screen: table + add/edit/delete form, backed
 * by {@link MockDataStore} for now. Input is validated here, before it would
 * ever reach the database, per the proposal's split of responsibilities.
 *
 * TODO (once J'Isabelle's DepartmentDAO is ready, Day 2-3): replace the
 * MockDataStore calls below with DAO calls; the table/form wiring itself
 * shouldn't need to change since it already works against the Department
 * model class.
 */
public class DepartmentController {

    @FXML
    private TableView<Department> departmentTable;
    @FXML
    private TableColumn<Department, String> nameColumn;
    @FXML
    private TableColumn<Department, String> parentColumn;
    @FXML
    private TableColumn<Department, String> employeeCountColumn;
    @FXML
    private TableColumn<Department, String> costColumn;
    @FXML
    private TableColumn<Department, String> budgetColumn;
    @FXML
    private TableColumn<Department, String> varianceColumn;

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Department> parentComboBox;
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
    private final ObservableList<Department> departments = FXCollections.observableArrayList();

    private Department selected;

    @FXML
    private void initialize() {
        departments.setAll(store.getAllDepartments());
        departmentTable.setItems(departments);

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        parentColumn.setCellValueFactory(data -> {
            Department parent = data.getValue().getParent();
            return new SimpleStringProperty(parent == null ? "(root)" : parent.getName());
        });
        employeeCountColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getEmployees().size())));
        costColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("$%,.2f", data.getValue().calculateBudget())));
        budgetColumn.setCellValueFactory(data -> {
            Budget b = data.getValue().getActiveBudget();
            return new SimpleStringProperty(b == null ? "—" : String.format("$%,.2f", b.getAllocatedAmount()));
        });
        varianceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%+,.2f", data.getValue().getBudgetVariance())));
        varianceColumn.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
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

        parentComboBox.setItems(departments);
        parentComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Department d) {
                return d == null ? "(none — top-level department)" : d.getName();
            }

            @Override
            public Department fromString(String s) {
                return null;
            }
        });

        departmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            selected = sel;
            populateForm(sel);
        });

        handleClear();
    }

    private void populateForm(Department d) {
        if (d == null) {
            handleClear();
            return;
        }
        formTitleLabel.setText("Edit Department");
        nameField.setText(d.getName());
        parentComboBox.setValue(d.getParent());
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        validationLabel.setText("");
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            validationLabel.setText("Department name is required.");
            return;
        }
        Department parent = parentComboBox.getValue();
        Department newDept = new Department(store.nextDepartmentId(), name, null);
        if (parent != null) {
            parent.addChild(newDept);
        }
        store.getAllDepartments().add(newDept);
        if (parent == null) {
            store.getRootDepartments().add(newDept);
        }
        departments.setAll(store.getAllDepartments());
        departmentTable.getSelectionModel().select(newDept);
        handleClear();
    }

    @FXML
    private void handleUpdate() {
        if (selected == null) {
            return;
        }
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            validationLabel.setText("Department name is required.");
            return;
        }
        Department newParent = parentComboBox.getValue();
        if (newParent == selected) {
            validationLabel.setText("A department cannot be its own parent.");
            return;
        }
        selected.setName(name);
        if (selected.getParent() != newParent) {
            if (selected.getParent() != null) {
                selected.getParent().getChildren().remove(selected);
            } else {
                store.getRootDepartments().remove(selected);
            }
            if (newParent != null) {
                newParent.addChild(selected);
            } else {
                selected.setParent(null);
                store.getRootDepartments().add(selected);
            }
        }
        departmentTable.refresh();
        parentComboBox.setItems(FXCollections.observableArrayList(departments));
    }

    @FXML
    private void handleDelete() {
        if (selected == null) {
            return;
        }
        if (!selected.getChildren().isEmpty() || !selected.getEmployees().isEmpty()) {
            validationLabel.setText("Can't delete: reassign or remove its employees / "
                    + "child departments first.");
            return;
        }
        if (selected.getParent() != null) {
            selected.getParent().getChildren().remove(selected);
        } else {
            store.getRootDepartments().remove(selected);
        }
        store.getAllDepartments().remove(selected);
        departments.setAll(store.getAllDepartments());
        handleClear();
    }

    @FXML
    private void handleClear() {
        selected = null;
        formTitleLabel.setText("Add Department");
        nameField.clear();
        parentComboBox.setValue(null);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        validationLabel.setText("");
        departmentTable.getSelectionModel().clearSelection();
    }
}
