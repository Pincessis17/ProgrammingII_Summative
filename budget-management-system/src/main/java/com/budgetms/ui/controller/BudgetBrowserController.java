package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import com.budgetms.model.OrgUnit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import com.budgetms.util.CurrencyFormatter;

public class BudgetBrowserController {

    @FXML private TreeView<OrgUnit> orgTree;
    @FXML private Label nameLabel;
    @FXML private Label headcountLabel;
    @FXML private Label costLabel;

    @FXML
    public void initialize() {
        TreeItem<OrgUnit> root = new TreeItem<>();
        root.setExpanded(true);

        for (Department department : AppState.departments) {
            if (!isChildOfAny(department)) {
                root.getChildren().add(buildNode(department));
            }
        }

        orgTree.setRoot(root);
        orgTree.setShowRoot(false);

        // Without this, the tree falls back to Object.toString() and shows
        // raw class/hashcode text instead of a readable name. This keeps that
        // display concern in the UI layer instead of the model classes.
        orgTree.setCellFactory(tv -> new TreeCell<OrgUnit>() {
            @Override
            protected void updateItem(OrgUnit unit, boolean empty) {
                super.updateItem(unit, empty);
                getStyleClass().removeAll("org-department-cell", "org-employee-cell");

                if (empty || unit == null) {
                    setText(null);
                } else {
                    setText(unit.getName());
                    getStyleClass().add(
                            unit instanceof Department ? "org-department-cell" : "org-employee-cell");
                }
            }
        });

        orgTree.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                showDetails(newItem.getValue());
            }
        });
    }

    private boolean isChildOfAny(Department department) {
        for (Department other : AppState.departments) {
            if (other.getChildren().contains(department)) {
                return true;
            }
        }
        return false;
    }

    private TreeItem<OrgUnit> buildNode(Department department) {
        TreeItem<OrgUnit> node = new TreeItem<>(department);
        // Start collapsed so the panel sees one level at a time when clicked
        // through during the demo, instead of the whole tree at once.
        node.setExpanded(false);

        for (Department child : department.getChildren()) {
            node.getChildren().add(buildNode(child));
        }

        for (Employee employee : department.getEmployees()) {
            node.getChildren().add(new TreeItem<>(employee));
        }

        return node;
    }

    private void showDetails(OrgUnit unit) {
        nameLabel.setText("Name: " + unit.getName());
        headcountLabel.setText("Total Employees: " + unit.getHeadcount());
        costLabel.setText("Cost: " + CurrencyFormatter.format(unit.calculateBudget()));
    }
}
