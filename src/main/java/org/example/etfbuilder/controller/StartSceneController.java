package org.example.etfbuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.example.etfbuilder.ETFBuilderApplication;

import java.time.YearMonth;

public class StartSceneController {

    private ETFBuilderApplication app;
    @FXML
    private ComboBox<String> startMonthComboBox;
    @FXML
    private ComboBox<String> startYearComboBox;

    public void initializeScene(ETFBuilderApplication app) {
        this.app = app;

        String[] months = new String[]{"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"};
        String[] years = new String[14];
        int year = 2010;
        for (int i = 0; i < 14; i++) {
            years[i] = String.valueOf(year);
            year++;
        }
        startMonthComboBox.setStyle("-fx-font: 14px \"Arial\";");
        startYearComboBox.setStyle("-fx-font: 14px \"Arial\";");
        startMonthComboBox.getItems().addAll(months);
        startYearComboBox.getItems().addAll(years);
    }

    @FXML
    public void startSimulation() {
        YearMonth startDate = YearMonth.parse(startYearComboBox.getValue() + "-"
                + startMonthComboBox.getValue());

        app.setSimStartDate(startDate);
        app.switchToCreateETFMainScene();
    }

}
