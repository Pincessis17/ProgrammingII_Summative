package com.budgetms.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DepartmentRecursionTest {

    @Test
    void getHeadcount_departmentWithNoEmployeesOrChildren_returnsZero() {
        Department department = new Department(1, "Empty Department", null);

        int result = department.getHeadcount();

        assertEquals(0, result);
    }

    @Test
    void getHeadcount_departmentWithEmployeesNoChildren_countsActiveEmployees() {
        Department department = new Department(1, "Engineering", null);

        Employee emp1 = new Employee(1, "Alice", "Developer", 3000.0, 1, true);
        Employee emp2 = new Employee(2, "Bob", "Developer", 2500.0, 1, false); // inactive
        department.addEmployee(emp1);
        department.addEmployee(emp2);

        int result = department.getHeadcount();

        // Only Alice counts — Bob is inactive
        assertEquals(1, result);
    }

    @Test
    void getHeadcount_nestedDepartments_countsAcrossAllLevels() {
        Department faculty = new Department(1, "Faculty of Computing", null);
        Department cs = new Department(2, "Computer Science", null);
        Department software = new Department(3, "Software Engineering", null);

        faculty.addChild(cs);
        cs.addChild(software);

        faculty.addEmployee(new Employee(1, "Dean", "Administrator", 5000.0, 1, true));
        cs.addEmployee(new Employee(2, "Lecturer", "Lecturer", 3000.0, 2, true));
        software.addEmployee(new Employee(3, "Lecturer", "Lecturer", 2800.0, 3, true));

        int result = faculty.getHeadcount();

        // 1 person at each of the 3 levels
        assertEquals(3, result);
    }
}