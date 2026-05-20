package org.example.budgetinsight.model;

public enum ExpenseCategory {
    FOOD, TRANSPORT, UTILITIES, ENTERTAINMENT, HEALTH, OTHER;

    public String display() {
        String n = name();
        return n.charAt(0) + n.substring(1).toLowerCase();
    }

    public String color() {
        return switch (this) {
            case FOOD          -> "#f97066";
            case TRANSPORT     -> "#fbbf24";
            case UTILITIES     -> "#34d399";
            case ENTERTAINMENT -> "#a78bfa";
            case HEALTH        -> "#60a5fa";
            case OTHER         -> "#94a3b8";
        };
    }

    public String emoji() {
        return switch (this) {
            case FOOD          -> "🍽";
            case TRANSPORT     -> "🚗";
            case UTILITIES     -> "⚡";
            case ENTERTAINMENT -> "🎬";
            case HEALTH        -> "🏥";
            case OTHER         -> "📦";
        };
    }
}