package org.example.budgetinsight;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.service.ExpenseService;
import org.example.budgetinsight.util.ExpenseDialog;
import org.example.budgetinsight.util.SceneNavigator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML private Label monthExpensesLabel;
    @FXML private Label todayTotalLabel;
    @FXML private Label todayCountLabel;
    @FXML private BarChart<String, Number> dailyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final ExpenseService expenseService = new ExpenseService();

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        List<Expense> all = expenseService.getAllExpenses();
        LocalDate today = LocalDate.now();

        BigDecimal todayTotal = all.stream()
                .filter(e -> e.getDate().equals(today))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long todayCount = all.stream().filter(e -> e.getDate().equals(today)).count();

        BigDecimal monthTotal = all.stream()
                .filter(e -> e.getDate().getYear() == today.getYear()
                          && e.getDate().getMonth() == today.getMonth())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        todayTotalLabel.setText(String.format("%.2f Lei", todayTotal));
        todayCountLabel.setText(todayCount + " tranzactii");
        monthExpensesLabel.setText(String.format("%.2f Lei", monthTotal));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            BigDecimal dayTotal = all.stream()
                    .filter(e -> e.getDate().equals(day))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            series.getData().add(new XYChart.Data<>(
                    day.getDayOfMonth() + "/" + day.getMonthValue(), dayTotal));
        }

        dailyChart.getData().clear();
        dailyChart.getData().add(series);
    }

    @FXML private void onNavDashboard() {}
    @FXML private void onNavExpenses() { SceneNavigator.navigateTo("expenses-view.fxml"); }
    @FXML private void onNavReports()  { SceneNavigator.navigateTo("reports-view.fxml"); }

    @FXML
    private void onAddExpense() {
        ExpenseDialog.show(null).ifPresent(expense -> {
            expenseService.addExpense(expense);
            loadStats();
        });
    }
}