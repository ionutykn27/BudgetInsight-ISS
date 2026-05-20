package org.example.budgetinsight;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.model.ExpenseCategory;
import org.example.budgetinsight.service.ExpenseService;
import org.example.budgetinsight.util.ExpenseDialog;
import org.example.budgetinsight.util.SceneNavigator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class ExpensesController {

    @FXML private FlowPane pillsPane;
    @FXML private StackPane chartStack;
    @FXML private PieChart categoryChart;
    @FXML private VBox chartLegend;
    @FXML private Label subtitleLabel;
    @FXML private HBox filterRow;
    @FXML private Button filterToggleBtn;
    @FXML private TableView<Expense> expensesTable;
    @FXML private TableColumn<Expense, LocalDate> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, BigDecimal> amountColumn;
    @FXML private TextField minAmountField;
    @FXML private TextField maxAmountField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Label countLabel;
    @FXML private Label footerTotalLabel;

    private final ExpenseService expenseService = new ExpenseService();
    private final ObservableList<Expense> expensesList = FXCollections.observableArrayList();
    private ToggleGroup pillGroup;
    private ExpenseCategory selectedCategory = null;
    private Label donutTotalLabel;

    private static final String CARD_BG = "#14142a";

    @FXML
    public void initialize() {
        expensesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupColumns();
        buildCategoryPills();
        loadData();
        setupDonutHole();
    }

    // ===== CATEGORY PILLS =====

    private void buildCategoryPills() {
        pillGroup = new ToggleGroup();

        ToggleButton allPill = createPill("Toate", null, null);
        allPill.setSelected(true);
        pillsPane.getChildren().add(allPill);

        for (ExpenseCategory cat : ExpenseCategory.values()) {
            pillsPane.getChildren().add(createPill(cat.emoji() + "  " + cat.display(), cat, cat.color()));
        }
    }

    private ToggleButton createPill(String text, ExpenseCategory category, String accentColor) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(pillGroup);
        btn.getStyleClass().add("category-pill");

        btn.setOnAction(e -> {
            if (btn.isSelected()) {
                selectedCategory = category;
                applyCurrentFilters();
            } else {
                btn.setSelected(true);
            }
        });

        if (accentColor != null) {
            btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    btn.setStyle(String.format(
                        "-fx-border-color: %s55; -fx-text-fill: %s;", accentColor, accentColor));
                } else {
                    btn.setStyle("");
                }
            });
        }

        return btn;
    }

    // ===== COLUMNS =====

    private void setupColumns() {
        // Data — doua randuri: zi mare + luna mica
        dateColumn.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDate()));
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setGraphic(null);
                    return;
                }
                VBox box = new VBox(0);
                box.setAlignment(Pos.CENTER_LEFT);
                Label day = new Label(String.valueOf(date.getDayOfMonth()));
                day.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
                Label month = new Label(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()).toUpperCase());
                month.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.3);");
                box.getChildren().addAll(day, month);
                setGraphic(box);
                setText(null);
            }
        });

        descriptionColumn.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDescription() != null
                        ? d.getValue().getDescription() : ""));

        // Categorie — badge colorat
        categoryColumn.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory().display()));
        categoryColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                ExpenseCategory cat = getTableRow() != null && getTableRow().getItem() != null
                        ? ((Expense) getTableRow().getItem()).getCategory()
                        : null;
                if (cat == null) { setText(item); setGraphic(null); return; }
                String color = cat.color();
                Label badge = new Label(cat.emoji() + "  " + item);
                badge.setStyle(String.format(
                    "-fx-background-color: %s18;" +
                    "-fx-border-color: %s33;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 3 10;" +
                    "-fx-text-fill: %s;" +
                    "-fx-font-size: 11px;",
                    color, color, color
                ));
                setGraphic(badge);
                setText(null);
            }
        });

        amountColumn.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAmount()));
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amt, boolean empty) {
                super.updateItem(amt, empty);
                if (empty || amt == null) { setText(null); return; }
                setText(String.format("%.2f", amt));
                setStyle("-fx-font-weight: bold; -fx-text-fill: rgba(255,255,255,0.9);");
            }
        });

        expensesTable.setItems(expensesList);
    }

    // ===== DONUT CHART =====

    private void setupDonutHole() {
        Platform.runLater(() -> {
            Circle hole = new Circle(62);
            hole.setFill(Color.web(CARD_BG));

            donutTotalLabel = new Label("0");
            donutTotalLabel.getStyleClass().add("chart-center-total");
            Label leiLbl = new Label("LEI");
            leiLbl.getStyleClass().add("chart-lei-label");

            VBox center = new VBox(1);
            center.setAlignment(Pos.CENTER);
            center.setMouseTransparent(true);
            center.getChildren().addAll(donutTotalLabel, leiLbl);

            chartStack.getChildren().addAll(hole, center);

            // Actualizeaza cu datele deja incarcate (loadData() ruleaza inainte de Platform.runLater)
            refreshDonutCenter(expensesList);
        });
    }

    private void refreshDonutCenter(List<Expense> list) {
        if (donutTotalLabel == null) return;
        BigDecimal total = list.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        donutTotalLabel.setText(String.format("%.0f", total));
    }

    private void refreshDonutChart(List<Expense> list) {
        Map<ExpenseCategory, Double> totals = new EnumMap<>(ExpenseCategory.class);
        for (ExpenseCategory c : ExpenseCategory.values()) totals.put(c, 0.0);
        list.forEach(e -> totals.merge(e.getCategory(), e.getAmount().doubleValue(), Double::sum));

        categoryChart.getData().clear();
        totals.forEach((cat, total) -> {
            if (total > 0) categoryChart.getData().add(new PieChart.Data(cat.display(), total));
        });

        // Coloreaza segmentele si adauga hover
        Platform.runLater(() -> {
            categoryChart.getData().forEach(data -> {
                String name = data.getName();
                ExpenseCategory cat = Arrays.stream(ExpenseCategory.values())
                        .filter(c -> c.display().equals(name))
                        .findFirst().orElse(null);
                if (cat != null && data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + cat.color() + ";");
                    addHoverEffect(data.getNode(), cat.color());
                }
            });
        });
    }

    private void addHoverEffect(javafx.scene.Node node, String colorHex) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(colorHex, 0.5));
        glow.setRadius(14);
        glow.setSpread(0.2);

        node.setOnMouseEntered(e -> {
            node.setEffect(glow);
            node.setScaleX(1.04);
            node.setScaleY(1.04);
        });
        node.setOnMouseExited(e -> {
            node.setEffect(null);
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    // ===== LEGEND =====

    private void buildChartLegend(List<Expense> list) {
        Map<ExpenseCategory, Double> totals = new EnumMap<>(ExpenseCategory.class);
        double grandTotal = 0;
        for (Expense e : list) {
            totals.merge(e.getCategory(), e.getAmount().doubleValue(), Double::sum);
            grandTotal += e.getAmount().doubleValue();
        }

        chartLegend.getChildren().clear();
        final double total = grandTotal;

        totals.entrySet().stream()
                .filter(en -> en.getValue() > 0)
                .sorted(Map.Entry.<ExpenseCategory, Double>comparingByValue().reversed())
                .forEach(en -> {
                    ExpenseCategory cat = en.getKey();
                    double pct = total > 0 ? (en.getValue() / total * 100) : 0;

                    HBox row = new HBox(8);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Rectangle dot = new Rectangle(10, 10);
                    dot.setArcWidth(3);
                    dot.setArcHeight(3);
                    dot.setFill(Color.web(cat.color()));

                    Label name = new Label(cat.display());
                    name.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 12px;");
                    name.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(name, Priority.ALWAYS);

                    Label pctLbl = new Label(String.format("%.0f%%", pct));
                    pctLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.3); -fx-font-size: 11px;");

                    Label amtLbl = new Label(String.format("%.0f Lei", en.getValue()));
                    amtLbl.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px; -fx-font-weight: bold;", cat.color()));

                    row.getChildren().addAll(dot, name, pctLbl, amtLbl);
                    chartLegend.getChildren().add(row);
                });
    }

    // ===== DATA LOADING =====

    private void loadData() {
        List<Expense> all = expenseService.getAllExpenses();
        expensesList.setAll(all);
        refreshDonutChart(all);
        refreshDonutCenter(all);
        buildChartLegend(all);
        refreshFooter(all);
        refreshSubtitle(all);
    }

    private void applyCurrentFilters() {
        BigDecimal minAmount = parseAmount(minAmountField.getText());
        BigDecimal maxAmount = parseAmount(maxAmountField.getText());
        LocalDate from = fromDatePicker.getValue();
        LocalDate to   = toDatePicker.getValue();

        List<Expense> filtered = expenseService.filterExpenses(
                minAmount, maxAmount, from, to, selectedCategory);

        expensesList.setAll(filtered);
        refreshDonutChart(filtered);
        refreshDonutCenter(filtered);
        buildChartLegend(filtered);
        refreshFooter(filtered);
        refreshSubtitle(filtered);
    }

    private void refreshFooter(List<Expense> list) {
        BigDecimal total = list.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        countLabel.setText(list.size() + " tranzacții afișate");
        footerTotalLabel.setText(String.format("Total: %.2f Lei", total));
    }

    private void refreshSubtitle(List<Expense> list) {
        int count = list.size();
        if (count == 0) {
            subtitleLabel.setText("Nicio cheltuială");
            return;
        }

        LocalDate from = fromDatePicker.getValue();
        LocalDate to   = toDatePicker.getValue();

        if (from != null || to != null) {
            // Filtru de data activ — arata intervalul selectat de utilizator
            Locale ro = new Locale("ro");
            String fromStr = from != null
                    ? from.getMonth().getDisplayName(TextStyle.FULL, ro) + " " + from.getYear()
                    : "început";
            String toStr = to != null
                    ? to.getMonth().getDisplayName(TextStyle.FULL, ro) + " " + to.getYear()
                    : "prezent";
            String period = fromStr.equals(toStr) ? fromStr : fromStr + " – " + toStr;
            subtitleLabel.setText(count + " tranzacții · " + period);
        } else {
            // Fara filtru de data — arata intervalul real al datelor afisate
            LocalDate min = list.stream().map(Expense::getDate).min(Comparator.naturalOrder()).orElse(null);
            LocalDate max = list.stream().map(Expense::getDate).max(Comparator.naturalOrder()).orElse(null);
            if (min != null && max != null) {
                Locale ro = new Locale("ro");
                String minStr = min.getMonth().getDisplayName(TextStyle.FULL, ro) + " " + min.getYear();
                String maxStr = max.getMonth().getDisplayName(TextStyle.FULL, ro) + " " + max.getYear();
                String period = minStr.equals(maxStr) ? minStr : minStr + " – " + maxStr;
                subtitleLabel.setText(count + " tranzacții · " + period);
            }
        }
    }

    // ===== CRUD ACTIONS =====

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
        if (selected == null) { showInfo("Selectați o cheltuială din listă pentru editare."); return; }
        ExpenseDialog.show(selected).ifPresent(expense -> {
            expenseService.updateExpense(expense);
            loadData();
        });
    }

    @FXML
    private void onDeleteExpense() {
        Expense selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo("Selectați o cheltuială din listă pentru ștergere."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Ștergeți cheltuiala selectată?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmare ștergere");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                expenseService.deleteExpense(selected.getId());
                loadData();
            }
        });
    }

    // ===== FILTER ACTIONS =====

    @FXML
    private void onToggleFilter() {
        boolean show = !filterRow.isVisible();
        filterRow.setVisible(show);
        filterRow.setManaged(show);
        filterToggleBtn.setText(show ? "⚙  Filtre ▲" : "⚙  Filtre");
    }

    @FXML
    private void onApplyFilter() {
        applyCurrentFilters();
    }

    @FXML
    private void onClearFilter() {
        minAmountField.clear();
        maxAmountField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        selectedCategory = null;
        pillGroup.getToggles().stream()
                .filter(t -> t instanceof ToggleButton)
                .findFirst()
                .ifPresent(t -> t.setSelected(true));
        loadData();
    }

    private BigDecimal parseAmount(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return new BigDecimal(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ===== NAVIGATION =====

    @FXML private void onNavDashboard() { SceneNavigator.navigateTo("dashboard-view.fxml"); }
    @FXML private void onNavExpenses()  {}
    @FXML private void onNavReports()   { SceneNavigator.navigateTo("reports-view.fxml"); }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
