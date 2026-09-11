package com.budgetms.ui.controller;

import com.budgetms.data.MockDataStore;
import com.budgetms.model.Budget;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import com.budgetms.model.OrgUnit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Wireframe for the Budget Browser tree screen — the recursive walk over the
 * OrgUnit composite is what the proposal calls calculateBudget/getHeadcount/
 * getBudgetVariance (section 4). Today this calls the placeholder recursive
 * methods on Department/Employee directly; once J'Isabelle's tested versions
 * land (Day 5) this screen should keep working unchanged, since it only
 * depends on the OrgUnit interface + Department/Budget getters.
 */
public class BudgetBrowserController {

    @FXML
    private TreeView<OrgUnit> orgTreeView;

    @FXML
    private Label selectedNameLabel;
    @FXML
    private Label selectedTypeLabel;
    @FXML
    private Label detailLine1;
    @FXML
    private Label detailLine2;
    @FXML
    private Label detailLine3;
    @FXML
    private Label detailLine4;
    @FXML
    private Label varianceLabel;

    private final MockDataStore store = MockDataStore.getInstance();

    @FXML
    private void initialize() {
        TreeItem<OrgUnit> invisibleRoot = new TreeItem<>();
        for (Department root : store.getRootDepartments()) {
            invisibleRoot.getChildren().add(buildTreeItem(root));
        }
        invisibleRoot.setExpanded(true);
        orgTreeView.setRoot(invisibleRoot);

        orgTreeView.setCellFactory(tv -> new javafx.scene.control.TreeCell<>() {
            @Override
            protected void updateItem(OrgUnit unit, boolean empty) {
                super.updateItem(unit, empty);
                if (empty || unit == null) {
                    setText(null);
                    return;
                }
                if (unit instanceof Department d) {
                    setText(String.format("%s  —  $%,.2f  (%d staff)", d.getName(),
                            d.calculateBudget(), d.getHeadcount()));
                } else {
                    Employee e = (Employee) unit;
                    setText(String.format("%s (%s)%s", e.getName(), e.getRole(),
                            e.isActive() ? "" : "  [inactive]"));
                }
            }
        });

        orgTreeView.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                showDetails(sel.getValue());
            }
        });

        clearDetails();
    }

    private TreeItem<OrgUnit> buildTreeItem(Department dept) {
        TreeItem<OrgUnit> item = new TreeItem<>(dept);
        for (Department child : dept.getChildren()) {
            item.getChildren().add(buildTreeItem(child));
        }
        for (Employee e : dept.getEmployees()) {
            item.getChildren().add(new TreeItem<>(e));
        }
        item.setExpanded(true);
        return item;
    }

    private void showDetails(OrgUnit unit) {
        if (unit instanceof Department d) {
            selectedNameLabel.setText(d.getName());
            selectedTypeLabel.setText("Department");
            detailLine1.setText(String.format("Direct employees: %d", d.getEmployees().size()));
            detailLine2.setText(String.format("Total headcount (incl. sub-departments): %d", d.getHeadcount()));
            detailLine3.setText(String.format("Total cost (incl. sub-departments): $%,.2f", d.calculateBudget()));
            Budget b = d.getActiveBudget();
            detailLine4.setText(b == null
                    ? "No active budget allocation"
                    : String.format("Active budget (%s): $%,.2f", b.getPeriod(), b.getAllocatedAmount()));
            if (b == null) {
                varianceLabel.setText("");
                varianceLabel.getStyleClass().removeAll("variance-positive", "variance-negative");
            } else {
                double variance = d.getBudgetVariance();
                varianceLabel.setText((variance >= 0 ? "Under allocation by $" : "Over allocation by $")
                        + String.format("%,.2f", Math.abs(variance)));
                varianceLabel.getStyleClass().removeAll("variance-positive", "variance-negative");
                varianceLabel.getStyleClass().add(variance >= 0 ? "variance-positive" : "variance-negative");
            }
        } else if (unit instanceof Employee e) {
            Department dept = store.findDepartmentById(e.getDepartmentId());
            selectedNameLabel.setText(e.getName());
            selectedTypeLabel.setText("Employee");
            detailLine1.setText("Role: " + e.getRole());
            detailLine2.setText("Department: " + (dept == null ? "—" : dept.getName()));
            detailLine3.setText(String.format("Salary: $%,.2f", e.getSalary()));
            detailLine4.setText("Status: " + (e.isActive() ? "Active" : "Inactive"));
            varianceLabel.setText("");
            varianceLabel.getStyleClass().removeAll("variance-positive", "variance-negative");
        }
    }

    private void clearDetails() {
        selectedNameLabel.setText("(nothing selected)");
        selectedTypeLabel.setText("");
        detailLine1.setText("");
        detailLine2.setText("");
        detailLine3.setText("");
        detailLine4.setText("");
        varianceLabel.setText("");
    }
}
