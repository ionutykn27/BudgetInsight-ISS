package org.example.budgetinsight.util;

import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.model.ExpenseCategory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<Expense> readCSVFile(String resourcePath) {
        List<Expense> expenses = new ArrayList<>();
        try (InputStream is = CSVReader.class.getResourceAsStream("/" + resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;
                Expense e = new Expense(
                        new BigDecimal(parts[3].trim()),
                        LocalDate.parse(parts[0].trim()),
                        parts[1].trim(),
                        ExpenseCategory.valueOf(parts[2].trim().toUpperCase())
                );
                expenses.add(e);
            }
        } catch (Exception ex) {
            System.err.println("CSV import error: " + ex.getMessage());
        }
        return expenses;
    }
}