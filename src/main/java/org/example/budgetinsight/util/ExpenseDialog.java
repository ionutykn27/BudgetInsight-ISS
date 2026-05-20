package org.example.budgetinsight.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class ExpenseDialog {

    public static Optional<Expense> show(Expense existing) {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Adaugare cheltuiala" : "Editare cheltuiala");
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 10, 24));

        TextField descField   = new TextField();
        descField.setPromptText("Ex: Cumparaturi Lidl");
        descField.setPrefWidth(220);

        TextField amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.setPrefWidth(120);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(160);

        ComboBox<ExpenseCategory> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(ExpenseCategory.values());
        categoryBox.setPrefWidth(160);
        categoryBox.setConverter(new StringConverter<>() {
            @Override public String toString(ExpenseCategory c) { return c == null ? "" : c.display(); }
            @Override public ExpenseCategory fromString(String s) { return null; }
        });

        if (existing != null) {
            descField.setText(existing.getDescription() != null ? existing.getDescription() : "");
            amountField.setText(existing.getAmount().toPlainString());
            datePicker.setValue(existing.getDate());
            categoryBox.setValue(existing.getCategory());
        }

        grid.add(new Label("Descriere:"),   0, 0);  grid.add(descField,   1, 0);
        grid.add(new Label("Suma (Lei):"),  0, 1);  grid.add(amountField, 1, 1);
        grid.add(new Label("Data:"),        0, 2);  grid.add(datePicker,  1, 2);
        grid.add(new Label("Categorie:"),   0, 3);  grid.add(categoryBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().trim().replace(",", "."));
                if (amount.compareTo(BigDecimal.ZERO) <= 0) return null;
                if (datePicker.getValue() == null || categoryBox.getValue() == null) return null;

                Expense result = (existing != null) ? existing : new Expense();
                result.setDescription(descField.getText().trim());
                result.setAmount(amount);
                result.setDate(datePicker.getValue());
                result.setCategory(categoryBox.getValue());
                return result;
            } catch (NumberFormatException ex) {
                return null;
            }
        });

        return dialog.showAndWait().filter(e -> e != null);
    }
}