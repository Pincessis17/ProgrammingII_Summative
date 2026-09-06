package com.budgetms.data;

import com.budgetms.model.Budget;
import com.budgetms.model.Department;
import com.budgetms.model.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Stand-in for J'Isabelle's DepartmentDAO / EmployeeDAO / BudgetDAO (JDBC
 * layer). Holds an in-memory sample org hierarchy so every screen has
 * something real to display and wire event handlers against on Day 1.
 *
 * Once the real DAOs land (Day 2-3 of the sync plan), the controllers should
 * swap calls to this class for calls to the DAOs — the screens are already
 * built against the same Department / Employee / Budget model classes, so
 * that swap should mostly be a constructor/wiring change, not a rewrite.
 */
public final class MockDataStore {

    private static final MockDataStore INSTANCE = new MockDataStore();

    private final List<Department> rootDepartments = new ArrayList<>();
    private final List<Department> allDepartments = new ArrayList<>();
    private final List<Employee> allEmployees = new ArrayList<>();
    private final List<Budget> allBudgets = new ArrayList<>();

    private int nextDeptId = 1;
    private int nextEmployeeId = 1;
    private int nextBudgetId = 1;

    private MockDataStore() {
        seed();
    }

    public static MockDataStore getInstance() {
        return INSTANCE;
    }

    private void seed() {
        Department computing = newDepartment("Faculty of Computing", null);
        Department cs = newDepartment("Computer Science", computing);
        Department it = newDepartment("Information Technology", computing);

        Department business = newDepartment("Faculty of Business", null);
        Department marketing = newDepartment("Marketing", business);
        Department finance = newDepartment("Finance", business);

        rootDepartments.add(computing);
        rootDepartments.add(business);

        newEmployee("Aris Okoye", "Senior Lecturer", 3200, cs, true);
        newEmployee("Naledi Dube", "Lecturer", 2600, cs, true);
        newEmployee("Kwame Boateng", "Lab Technician", 1800, cs, false);

        newEmployee("Fatima Rahman", "Network Administrator", 2400, it, true);
        newEmployee("Diego Ramirez", "Support Engineer", 2100, it, true);

        newEmployee("Chloe Nguyen", "Marketing Manager", 2900, marketing, true);
        newEmployee("Ben Osei", "Content Strategist", 2000, marketing, true);

        newEmployee("Priya Nair", "Finance Officer", 2700, finance, true);
        newEmployee("Liam O'Connor", "Accountant", 2300, finance, true);

        newBudget(computing, "2026-Q3", 12000);
        newBudget(cs, "2026-Q3", 8000);
        newBudget(it, "2026-Q3", 5000);
        newBudget(business, "2026-Q3", 9000);
        newBudget(marketing, "2026-Q3", 5500);
        newBudget(finance, "2026-Q3", 5200);
    }

    private Department newDepartment(String name, Department parent) {
        Department d = new Department(nextDeptId++, name, null);
        if (parent != null) {
            parent.addChild(d);
        }
        allDepartments.add(d);
        return d;
    }

    private Employee newEmployee(String name, String role, double salary, Department dept, boolean active) {
        Employee e = new Employee(nextEmployeeId++, name, role, salary, dept.getId(), active);
        dept.addEmployee(e);
        allEmployees.add(e);
        return e;
    }

    private Budget newBudget(Department dept, String period, double amount) {
        Budget b = new Budget(nextBudgetId++, dept.getId(), period, amount);
        dept.setActiveBudget(b);
        allBudgets.add(b);
        return b;
    }

    public List<Department> getRootDepartments() {
        return rootDepartments;
    }

    public List<Department> getAllDepartments() {
        return allDepartments;
    }

    public List<Employee> getAllEmployees() {
        return allEmployees;
    }

    public List<Budget> getAllBudgets() {
        return allBudgets;
    }

    public Department findDepartmentById(int id) {
        for (Department d : allDepartments) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public int nextDepartmentId() {
        return nextDeptId++;
    }

    public int nextEmployeeId() {
        return nextEmployeeId++;
    }

    public int nextBudgetId() {
        return nextBudgetId++;
    }
}
