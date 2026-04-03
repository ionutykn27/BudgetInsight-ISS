package org.example.budgetinsight;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class ReportsController {

    @FXML private BarChart<String, Number> incomeExpensesChart;

    @FXML
    public void initialize() {
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().addAll(
                new XYChart.Data<>("Jan", 4000),
                new XYChart.Data<>("Feb", 4000),
                new XYChart.Data<>("Mar", 4000)
        );

        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Expenses");
        expensesSeries.getData().addAll(
                new XYChart.Data<>("Jan", 2100),
                new XYChart.Data<>("Feb", 2650),
                new XYChart.Data<>("Mar", 2340)
        );

        incomeExpensesChart.getData().addAll(incomeSeries, expensesSeries);
    }
}