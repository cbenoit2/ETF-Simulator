package org.example.etfbuilder.controller;

import javafx.fxml.FXML;
import org.example.etfbuilder.ETFBuilderApplication;


public class CreateETFSceneController {

    private ETFBuilderApplication app;

    public void initializeScene(ETFBuilderApplication app) {
        this.app = app;
    }

    @FXML
    public void switchToCreateUserETFScene() {
        app.switchToCreateUserETFScene();
    }

    @FXML
    public void switchToCreateSystemETFScene() {
        app.switchToCreateSystemETFScene();
    }

    @FXML
    public void switchToPortfolioScene() {
        app.switchToPortfolioScene();
    }

}
