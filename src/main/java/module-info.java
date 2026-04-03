module org.example.budgetinsight {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.example.budgetinsight to javafx.fxml;
    exports org.example.budgetinsight;
}