package com.budgetms.dao;

// Thrown when an attempt is made to delete a Department that still has employees or child departments assigned to it.
public class DepartmentNotEmptyException extends RuntimeException {

    public DepartmentNotEmptyException(String message) {
        super(message);
    }
}