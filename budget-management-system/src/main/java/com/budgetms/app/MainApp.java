package com.budgetms.app;

import com.budgetms.dao.DepartmentDAO;
import com.budgetms.dao.DepartmentDAOImpl;
import com.budgetms.dao.EmployeeDAO;
import com.budgetms.dao.EmployeeDAOImpl;
import com.budgetms.db.SchemaInitializer;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SchemaInitializer.initializeSchema();

        DepartmentDAO departmentDAO = new DepartmentDAOImpl();
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();

        List<Department> departments = departmentDAO.findAll();

        // findAll() only builds the department tree itself; it never touches
        // the employee table. Without this, every Department starts with an
        // empty employee list and the Budget Browser hierarchy silently
        // shows no one under any department, even though the rows exist.
        Map<Integer, Department> departmentsById = new HashMap<>();
        for (Department department : departments) {
            departmentsById.put(department.getId(), department);
        }
        for (Employee employee : employeeDAO.findAll()) {
            Department owner = departmentsById.get(employee.getDepartmentId());
            if (owner != null) {
                owner.addEmployee(employee);
            }
        }

        AppState.departments.setAll(departments);

        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/MainShell.fxml")));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/app.css")).toExternalForm());

        stage.setTitle("Departmental Budget Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
