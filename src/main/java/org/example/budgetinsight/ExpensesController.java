package org.example.budgetinsight;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.model.ExpenseCategory;
import org.example.budgetinsight.service.ExpenseService;
import org.example.budgetinsight.util.ExpenseDialog;
import org.example.budgetinsight.util.SceneNavigator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ExpensesController {

    @FXML private PieChart categoryChart;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private TableView<Expense> expensesTable;
    @FXML private TableColumn<Expense, LocalDate> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, BigDecimal> amountColumn;
    @FXML private TextField minAmountField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    private final ExpenseService expenseService = new ExpenseService();
    private final ObservableList<Expense> expensesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupCategoryFilter();
        loadData();
    }

    private void setupColumns() {
        dateColumn.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDate()));
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.toString());
            }
        });

        descriptionColumn.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDescription() != null
                        ? d.getValue().getDescription() : ""));

        categoryColumn.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory().display()));

        amountColumn.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAmount()));
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(BigDecimal amt, boolean empty) {
                super.updateItem(amt, empty);
                setText(empty || amt == null ? null : String.format("%.2f", amt));
            }
        });

        expensesTable.setItems(expensesList);
    }

    private void setupCategoryFilter() {
        List<String> options = new ArrayList<>();
        options.add("Toate categoriile");
        Arrays.stream(ExpenseCategory.values()).map(ExpenseCategory::display).forEach(options::add);
        categoryFilter.setItems(FXCollections.observableArrayList(options));
        categoryFilter.getSelectionModel().selectFirst();
    }

    private void loadData() {
        expensesList.setAll(expenseService.getAllExpenses());
        refreshPieChart(expensesList);
    }

    private void refreshPieChart(List<Expense> list) {
        Map<ExpenseCategory, Double> totals = new EnumMap<>(ExpenseCategory.class);
        for (ExpenseCategory c : ExpenseCategory.values()) totals.put(c, 0.0);
        list.forEach(e -> totals.merge(e.getCategory(), e.getAmount().doubleValue(), Double::sum));

        categoryChart.getData().clear();
        totals.forEach((cat, total) -> {
            if (total > 0) categoryChart.getData().add(new PieChart.Data(cat.display(), total));
        });
    }

    @FXML
    private void onAddExpense() {
        ExpenseDialog.show(null).ifPresent(expense -> {
            expenseService.addExpense(expense);
            loadData();
        });
    }

    @FXML
    private void onEditExpense() {
        Expense selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo("Selectati o cheltuiala din lista pentru editare."); return; }
        ExpenseDialog.show(selected).ifPresent(expense -> {
            expenseService.updateExpense(expense);
            loadData();
        });
    }

    @FXML
    private void onDeleteExpense() {
        Expense selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo("Selectati o cheltuiala din lista pentru stergere."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Stergeti cheltuiala selectata?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmare stergere");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                expenseService.deleteExpense(selected.getId());
                loadData();
            }
        });
    }

    @FXML
    private void onApplyFilter() {
        List<Expense> all = expenseService.getAllExpenses();
        String minText = minAmountField.getText().trim();
        BigDecimal minAmount = minText.isEmpty() ? BigDecimal.ZERO
                : new BigDecimal(minText.replace(",", "."));
        LocalDate from = fromDatePicker.getValue();
        LocalDate to   = toDatePicker.getValue();
        String cat     = categoryFilter.getSelectionModel().getSelectedItem();

        List<Expense> filtered = all.stream()
                .filter(e -> e.getAmount().compareTo(minAmount) >= 0)
                .filter(e -> from == null || !e.getDate().isBefore(from))
                .filter(e -> to   == null || !e.getDate().isAfter(to))
                .filter(e -> cat  == null || cat.equals("Toate categoriile")
                          || e.getCategory().display().equals(cat))
                .collect(Collectors.toList());

        expensesList.setAll(filtered);
        refreshPieChart(filtered);
    }

    @FXML
    private void onClearFilter() {
        minAmountField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        categoryFilter.getSelectionModel().selectFirst();
        loadData();
    }

    @FXML private void onNavDashboard() { SceneNavigator.navigateTo("dashboard-view.fxml"); }
    @FXML private void onNavExpenses()  {}
    @FXML private void onNavReports()   { SceneNavigator.navigateTo("reports-view.fxml"); }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}