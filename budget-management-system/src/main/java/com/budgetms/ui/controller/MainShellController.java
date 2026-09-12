package com.budgetms.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainShellController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        showDepartments();
    }

    @FXML
    private void showDepartments() {
        loadScreen("/fxml/DepartmentView.fxml");
    }

    @FXML
    private void showEmployees() {
        loadScreen("/fxml/EmployeeView.fxml");
    }

    @FXML
    private void showBudgets() {
        loadScreen("/fxml/BudgetView.fxml");
    }

    @FXML
    private void showBudgetBrowser() {
        loadScreen("/fxml/BudgetBrowserView.fxml");
    }

    private void loadScreen(String fxmlPath) {
        try {
            Node screen = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(screen);
        } catch (IOException | RuntimeException e) {
            new Alert(Alert.AlertType.WARNING, "This screen isn't built yet.").showAndWait();
        }
    }
}