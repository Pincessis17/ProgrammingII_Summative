package com.budgetms.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DepartmentTest {

    @Test
    void calculateBudget_departmentWithNoEmployeesOrChildren_returnsZero() {
        Department department = new Department(1, "Empty Department", null);

        double result = department.calculateBudget();

        assertEquals(0.0, result);
    }

    @Test
    void calculateBudget_departmentWithEmployeesNoChildren_sumsEmployeeSalaries() {
        Department department = new Department(1, "Engineering", null);

        Employee emp1 = new Employee(1, "Alice", "Developer", 3000.0, 1, true);
        Employee emp2 = new Employee(2, "Bob", "Developer", 2500.0, 1, true);
        department.addEmployee(emp1);
        department.addEmployee(emp2);

        double result = department.calculateBudget();

        assertEquals(5500.0, result);
    }

    @Test
    void calculateBudget_nestedDepartments_sumsAcrossAllLevels() {
        Department faculty = new Department(1, "Faculty of Computing", null);
        Department cs = new Department(2, "Computer Science", null);
        Department software = new Department(3, "Software Engineering", null);

        faculty.addChild(cs);
        cs.addChild(software);

        Employee facultyEmp = new Employee(1, "Dean", "Administrator", 5000.0, 1, true);
        Employee csEmp = new Employee(2, "Lecturer", "Lecturer", 3000.0, 2, true);
        Employee softwareEmp = new Employee(3, "Lecturer", "Lecturer", 2800.0, 3, true);

        faculty.addEmployee(facultyEmp);
        cs.addEmployee(csEmp);
        software.addEmployee(softwareEmp);

        double result = faculty.calculateBudget();

        // 5000 (faculty's own) + 3000 (CS's own) + 2800 (Software's own)
        assertEquals(10800.0, result);
    }
}

