package com.budgetms.ui;

import com.budgetms.model.Department;
import com.budgetms.util.ValidationUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    @Test
    void requireNonBlank_rejectsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonBlank(null, "Name"));
    }

    @Test
    void requireNonBlank_rejectsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonBlank("   ", "Name"));
    }

    @Test
    void requireNonBlank_acceptsRealValue() {
        assertDoesNotThrow(() -> ValidationUtils.requireNonBlank("Finance", "Name"));
    }

    @Test
    void requirePositiveNumber_rejectsNonNumericText() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requirePositiveNumber("abc", "Salary"));
    }

    @Test
    void requirePositiveNumber_rejectsNegativeValue() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requirePositiveNumber("-500", "Salary"));
    }

    @Test
    void requirePositiveNumber_returnsParsedValue() {
        double result = ValidationUtils.requirePositiveNumber("2500.50", "Salary");
        assertEquals(2500.50, result, 0.001);
    }

    @Test
    void addChild_rejectsNestingBeyondMaxDepth() {
        Department current = new Department("Level 1");

        // Build a chain 6 levels deep - the maximum Department.addChild() allows
        for (int i = 2; i <= 6; i++) {
            Department next = new Department("Level " + i);
            current.addChild(next);
            current = next;
        }

        // A 7th level must be rejected
        Department tooDeep = new Department("Level 7");
        Department deepestSoFar = current;
        assertThrows(IllegalStateException.class, () -> deepestSoFar.addChild(tooDeep));
    }

    @Test
    void addChild_allowsNestingWithinMaxDepth() {
        Department parent = new Department("Parent");
        Department child = new Department("Child");

        assertDoesNotThrow(() -> parent.addChild(child));
        assertTrue(parent.getChildren().contains(child));
    }
}
