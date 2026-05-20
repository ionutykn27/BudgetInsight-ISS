package org.example.budgetinsight.service;

import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseService {

    private final ExpenseRepository repository = new ExpenseRepository();

    public Expense addExpense(Expense expense) {
        validateData(expense);
        return repository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
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