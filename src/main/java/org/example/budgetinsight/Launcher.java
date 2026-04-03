package org.example.budgetinsight;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        Application.launch(HelloApplication.class, args);
    }
}
