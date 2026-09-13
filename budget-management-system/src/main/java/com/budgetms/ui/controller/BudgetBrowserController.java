package com.budgetms.ui.controller;

import com.budgetms.app.AppState;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import com.budgetms.model.OrgUnit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
        headcountLabel.setText("Headcount: " + unit.getHeadcount());
        costLabel.setText("Cost: " + unit.calculateBudget());
    }
}
