package com.budgetms.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

/**
 * Controls the sidebar navigation and swaps the four screens in and out of
 * {@code contentArea}. Each nav button is a ToggleButton so the active
 * screen stays visibly highlighted (see .nav-button:selected in app.css).
 */
public class MainShellController {

    @FXML
    private StackPane contentArea;

    @FXML
    private ToggleButton departmentsNavButton;
    @FXML
    private ToggleButton employeesNavButton;
    @FXML
    private ToggleButton budgetsNavButton;
    @FXML
    private ToggleButton budgetBrowserNavButton;

    @FXML
    private void initialize() {
        showDepartments();
    }

    @FXML
    private void showDepartments() {
        selectOnly(departmentsNavButton);
        loadScreen("/fxml/DepartmentView.fxml");
    }

    @FXML
    private void showEmployees() {
        selectOnly(employeesNavButton);
        loadScreen("/fxml/EmployeeView.fxml");
    }

    @FXML
    private void showBudgets() {
        selectOnly(budgetsNavButton);
        loadScreen("/fxml/BudgetView.fxml");
    }

    @FXML
    private void showBudgetBrowser() {
        selectOnly(budgetBrowserNavButton);
        loadScreen("/fxml/BudgetBrowserView.fxml");
    }

    private void selectOnly(ToggleButton active) {
        for (ToggleButton b : new ToggleButton[]{departmentsNavButton, employeesNavButton,
                budgetsNavButton, budgetBrowserNavButton}) {
            b.setSelected(b == active);
        }
    }

    private void loadScreen(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Node screen = loader.load();
            contentArea.getChildren().setAll(screen);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Could not load screen: " + fxmlPath + "\n" + e.getMessage()).showAndWait();
        }
    }
}
