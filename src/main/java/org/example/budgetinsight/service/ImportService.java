package org.example.budgetinsight.service;

import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.repository.ExpenseRepository;
import org.example.budgetinsight.util.CSVReader;

import java.util.List;

public class ImportService {

    private final ExpenseRepository repository = new ExpenseRepository();

    public void importIfEmpty() {
        if (repository.isEmpty()) {
            List<Expense> expenses = CSVReader.readCSVFile("expenses.csv");
            if (!expenses.isEmpty()) {
                repository.saveAll(expenses);
                System.out.println("Import CSV: " + expenses.size() + " cheltuieli importate.");
            }
        }
    }
}