package org.example.budgetinsight;

import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.budgetinsight.service.ImportService;
import org.example.budgetinsight.util.HibernateUtil;
import org.example.budgetinsight.util.SceneNavigator;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        SceneNavigator.setStage(stage);
        stage.setTitle("BudgetInsight");

        try {
            new ImportService().importIfEmpty();
        } catch (Exception e) {
            System.err.println("Conexiune DB esuat, aplicatia porneste fara date: " + e.getMessage());
        }

        SceneNavigator.navigateTo("dashboard-view.fxml");
        stage.show();
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }
}