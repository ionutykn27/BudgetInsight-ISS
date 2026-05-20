package org.example.budgetinsight.service;

import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.model.ExpenseCategory;
import org.example.budgetinsight.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseService {

    private final ExpenseRepository repository = new ExpenseRepository();

    public Expense addExpense(Expense expense) {
        validateData(expense);
        return repository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public List<Expense> filterExpenses(BigDecimal minAmount, BigDecimal maxAmount,
                                        LocalDate from, LocalDate to,
                                        ExpenseCategory category) {
        return repository.findAll().stream()
                .filter(e -> minAmount == null || e.getAmount().compareTo(minAmount) >= 0)
                .filter(e -> maxAmount == null || e.getAmount().compareTo(maxAmount) <= 0)
                .filter(e -> from == null || !e.getDate().isBefore(from))
                .filter(e -> to   == null || !e.getDate().isAfter(to))
                .filter(e -> category == null || e.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Expense updateExpense(Expense expense) {
        validateData(expense);
        return repository.update(expense);
    }

    public void deleteExpense(Long id) {
        repository.delete(id);
    }

    private void validateData(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Suma trebuie sa fie pozitiva.");
        if (expense.getDate() == null)
            throw new IllegalArgumentException("Data este obligatorie.");
        if (expense.getCategory() == null)
            throw new IllegalArgumentException("Categoria este obligatorie.");
    }
}