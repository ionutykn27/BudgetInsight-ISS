package org.example.budgetinsight.model;

public enum ExpenseCategory {
    FOOD, TRANSPORT, UTILITIES, ENTERTAINMENT, HEALTH, OTHER;

    public String display() {
        String n = name();
        return n.charAt(0) + n.substring(1).toLowerCase();
    }
}