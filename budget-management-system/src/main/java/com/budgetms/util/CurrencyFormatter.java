package com.budgetms.util;

public class CurrencyFormatter {

    public static String format(double amount) {
        return String.format("MUR %,.2f", amount);
    }
}
