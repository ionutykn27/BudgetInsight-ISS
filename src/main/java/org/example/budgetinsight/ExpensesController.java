package org.example.budgetinsight;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;

public class ExpensesController {

    @FXML private PieChart categoryChart;
    @FXML private ComboBox<String> categoryFilter;

    @FXML
    public void initialize() {
        categoryChart.getData().addAll(
                new PieChart.Data("Food",          450.0),
                new PieChart.Data("Transport",     230.0),
                new PieChart.Data("Utilities",     180.0),
                new PieChart.Data("Entertainment", 320.0),
                new PieChart.Data("Health",        160.0),
                new PieChart.Data("Other",         100.0)
        );

        categoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "Food", "Transport",
                "Utilities", "Entertainment", "Health", "Other"
        ));
        categoryFilter.getSelectionModel().selectFirst();
    }
}